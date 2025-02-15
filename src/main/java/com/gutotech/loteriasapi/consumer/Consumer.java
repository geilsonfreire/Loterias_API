package com.gutotech.loteriasapi.consumer;

// Imports dos pacotes necessários
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

// Imports dos pacotes necessários
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

// Imports dos pacotes necessários
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

// Imports dos models necessários
import com.gutotech.loteriasapi.model.Estado;
import com.gutotech.loteriasapi.model.Loteria;
import com.gutotech.loteriasapi.model.MunicipioUFGanhadores;
import com.gutotech.loteriasapi.model.Premiacao;
import com.gutotech.loteriasapi.model.Resultado;
import com.gutotech.loteriasapi.model.ResultadoId;
import com.gutotech.loteriasapi.util.SSLHelper;

@Component
public class Consumer {
    // Delay entre as requisições
    private static final long DELAY_BETWEEN_REQUESTS = 1000; // 1 segundo
    // Método para pegar o resultado da loteria
    public Resultado getResultado(String loteria, int concurso) throws Exception {
        return getResultado(loteria, String.valueOf(concurso));
    }

    public Resultado getResultado(String loteria, String concurso) throws Exception {
        // Adiciona delay para evitar bloqueio
        Thread.sleep(DELAY_BETWEEN_REQUESTS);
        return servicebus2(loteria, concurso);
    }

    private Resultado servicebus2(String loteria, String concurso) throws Exception {
        String baseUrl = "https://servicebus2.caixa.gov.br/portaldeloterias/api/";

        if (concurso == null) {
            concurso = "";
        }

        Document doc = SSLHelper.getConnection(baseUrl + loteria + "/" + concurso).get();
        if (doc == null) {
            return null;
        }

        String bodyText = doc.select("body").text();
        if (bodyText == null || bodyText.isBlank()) {
            return null;
        }

        JSONObject jsonObject = new JSONObject(bodyText);

        ResultadoId resultadoId = new ResultadoId(loteria, jsonObject.getInt("numero"));

        Resultado resultado = new Resultado(resultadoId);

        // DATA
        String data = jsonObject.optString("dataApuracao", null);
        if (data != null) {
            resultado.setData(data);
        }

        // LOCAL
        String local = jsonObject.optString("localSorteio", "") + " em " + jsonObject.optString("nomeMunicipioUFSorteio", "");
        resultado.setLocal(local);

        // DEZENAS
        if (jsonObject.has("dezenasSorteadasOrdemSorteio") && !jsonObject.isNull("dezenasSorteadasOrdemSorteio")) {
            List<String> dezenasOrdemSorteio = jsonObject.getJSONArray("dezenasSorteadasOrdemSorteio")
                    .toList()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            resultado.setDezenasOrdemSorteio(dezenasOrdemSorteio);
        }

        if (jsonObject.has("listaDezenas") && !jsonObject.isNull("listaDezenas")) {
            List<String> dezenas = jsonObject.getJSONArray("listaDezenas")
                    .toList()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());

            if (jsonObject.has("listaDezenasSegundoSorteio") && !jsonObject.isNull("listaDezenasSegundoSorteio")) {
                List<String> dezenas2 = jsonObject.getJSONArray("listaDezenasSegundoSorteio")
                        .toList()
                        .stream()
                        .map(Object::toString).toList();
                dezenas.addAll(dezenas2);
            }
            resultado.setDezenas(dezenas);
        }

        // TREVOS - +MILIONARIA
        if (jsonObject.has("trevosSorteados") && !jsonObject.isNull("trevosSorteados")) {
            List<String> trevos = jsonObject.getJSONArray("trevosSorteados")
                    .toList()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            resultado.setTrevos(trevos);
        }

        // TIME DE CORACAO E MES DA SORTE
        if (jsonObject.has("nomeTimeCoracaoMesSorte") && !jsonObject.isNull("nomeTimeCoracaoMesSorte")) {
            String nomeTimeCoracaoMesSorte = jsonObject.getString("nomeTimeCoracaoMesSorte");

            if (loteria.equals(Loteria.DIA_DE_SORTE.toString())) {
                try {
                    int numeroMes = Integer.parseInt(nomeTimeCoracaoMesSorte);

                    List<String> meses = List.of("Janeiro", "Fevereiro", "Março", "Abril", "Maio",
                            "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro",
                            "Dezembro");

                    nomeTimeCoracaoMesSorte = meses.get(numeroMes - 1);
                } catch (NumberFormatException e) {
                    // Handle exception
                }

                resultado.setMesSorte(nomeTimeCoracaoMesSorte);
            } else if (loteria.equals(Loteria.TIMEMANIA.toString())) {
                resultado.setTimeCoracao(nomeTimeCoracaoMesSorte);
            }
        }

        // PREMIACOES
        if (!jsonObject.has("listaRateioPremio") || jsonObject.isNull("listaRateioPremio")) {
            return new Resultado(resultadoId);
        }

        List<Premiacao> premiacoes = jsonObject.getJSONArray("listaRateioPremio").toList().stream()
                .map(obj -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) obj;
                    JSONObject jsonObject2 = new JSONObject(map);

                    Premiacao premiacao = new Premiacao();
                    premiacao.setDescricao(jsonObject2.getString("descricaoFaixa"));
                    premiacao.setFaixa(jsonObject2.getInt("faixa"));
                    premiacao.setGanhadores(jsonObject2.getInt("numeroDeGanhadores"));
                    premiacao.setValorPremio(jsonObject2.getDouble("valorPremio"));

                    return premiacao;
                }).toList();

        resultado.getPremiacoes().addAll(premiacoes);

        // ESTADOS PREMIADOS
        if (jsonObject.has("listaMunicipioUFGanhadores") && !jsonObject.isNull("listaMunicipioUFGanhadores")) {
            List<MunicipioUFGanhadores> municipiosGanhadores = jsonObject
                    .getJSONArray("listaMunicipioUFGanhadores").toList().stream().map(obj -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                JSONObject jsonObject2 = new JSONObject(map);

                MunicipioUFGanhadores mg = new MunicipioUFGanhadores();
                mg.setGanhadores(jsonObject2.getInt("ganhadores"));
                mg.setMunicipio(jsonObject2.getString("municipio"));
                mg.setNomeFatansiaUL(jsonObject2.getString("nomeFatansiaUL"));
                mg.setPosicao(jsonObject2.getInt("posicao"));
                mg.setSerie(jsonObject2.getString("serie"));
                mg.setUf(jsonObject2.getString("uf"));

                return mg;
            }).collect(Collectors.toList());

            resultado.setLocalGanhadores(municipiosGanhadores);
        }

        // OBSERVACAO
        if (jsonObject.has("observacao") && !jsonObject.isNull("observacao")) {
            String observacao = jsonObject.getString("observacao");
            resultado.setObservacao(observacao);
        }

        // ACUMULADO
        resultado.setAcumulou(jsonObject.getBoolean("acumulado"));

        // DATA PROXIMO CONCURSO
        String dataProxConcurso = jsonObject.optString("dataProximoConcurso", null);
        resultado.setDataProximoConcurso(dataProxConcurso);

        resultado.setValorArrecadado(jsonObject.getDouble("valorArrecadado"));
        resultado.setValorAcumuladoConcurso_0_5(jsonObject.getDouble("valorAcumuladoConcurso_0_5"));
        resultado.setValorAcumuladoConcursoEspecial(
                jsonObject.getDouble("valorAcumuladoConcursoEspecial"));
        resultado.setValorAcumuladoProximoConcurso(
                jsonObject.getDouble("valorAcumuladoProximoConcurso"));
        resultado.setValorEstimadoProximoConcurso(
                jsonObject.getDouble("valorEstimadoProximoConcurso"));

        return resultado;
    }

    @SuppressWarnings("unused")
    @Deprecated
    private Resultado sorteOnline(String loteria, String concurso) throws IOException {
        String baseUrl = "https://www.sorteonline.com.br/";

        if (concurso == null) {
            concurso = "";
        }

        System.out.println(loteria + " - " + concurso);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Document doc = SSLHelper.getConnection(baseUrl + loteria + "/resultados/" + concurso).get();
        if (doc == null) {
            return null;
        }

        Element resultElement = doc.getElementById("DivDeVisibilidade[0]");
        if (resultElement == null) {
            return null;
        }

        Resultado resultado = new Resultado(
                new ResultadoId(loteria, concurso.isEmpty() ? 0 : Integer.parseInt(concurso)));

        // Data
        String data = resultElement.getElementsByClass("header-resultados__datasorteio").text();
        if (data.equalsIgnoreCase("HOJE")) {
            data = dateFormat.format(new Date());
        }
        resultado.setData(data);

        // Local
        resultado.setLocal(
                resultElement.getElementsByClass("header-resultados__local-sorteio").text());

        // Dezenas
        Elements numbers = resultElement.select(".bg");
        for (Element element : numbers) {
            try {
                Integer.parseInt(element.text());
            } catch (NumberFormatException | NullPointerException e) {
                continue;
            }
            resultado.getDezenas().add(element.text());
        }

        // Premiacoes
        Elements premiacoesTrs = resultElement.select(".block-table .result .tr");
        for (Element tr : premiacoesTrs) {
            if (tr.classNames().size() > 1) {
                continue;
            }

            Premiacao premiacao = new Premiacao();

            premiacao.setDescricao(tr.getElementsByClass("td").get(0).text());

            try {
                premiacao.setGanhadores(Integer.parseInt(
                        tr.getElementsByClass("td").get(1).text().replaceAll("[^\\d.]", "")));
            } catch (NumberFormatException | NullPointerException e) {
                premiacao.setGanhadores(0);
            }
            resultado.getPremiacoes().add(premiacao);
        }

        // Estados premiados
        Element buttonWin = resultElement.getElementsByClass("button-win").first();
        if (buttonWin != null) {
            String json = buttonWin.attr("data-estados-premiados");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<Estado> estados = objectMapper.readValue(json, new TypeReference<List<Estado>>() {
            });
            resultado.setEstadosPremiados(estados);
        }

        // Acumulado
        resultado.setAcumulou(!resultElement.getElementsByClass("acumulado").isEmpty());

        // Data Proximo Concurso
        String dataProxConcurso = resultElement
                .getElementsByClass("foother-resultados__data-sorteio").text();

        if (dataProxConcurso != null) {
            if (dataProxConcurso.equalsIgnoreCase("hoje")) {
                dataProxConcurso = dateFormat.format(new Date());
            } else if (dataProxConcurso.equalsIgnoreCase("amanhã")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 1);
                dataProxConcurso = dateFormat.format(calendar.getTime());
            }
        }
        resultado.setDataProximoConcurso(dataProxConcurso);

        // Time de coracao
        if (!resultElement.getElementsByClass("lnr-heart").isEmpty()) {
            resultado.setTimeCoracao(resultElement.getElementsByClass("time-coracao").text());
        }

        // Mes de sorte
        if (!resultElement.getElementsByClass("lnr-calendar-full").isEmpty()) {
            resultado.setMesSorte(resultElement.getElementsByClass("time-coracao").text());
        }

        return resultado;
    }

}
