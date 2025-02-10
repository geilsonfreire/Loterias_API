package com.gutotech.loteriasapi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estados")
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String uf;
    private String vencedores;
    private String latitude;
    private String longitude;

    @ElementCollection
    @Embedded
    private List<Cidade> cidades = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonGetter("nome")
    public String getNome() {
        return nome;
    }

    @JsonSetter("NomeEstado")
    public void setNome(String nome) {
        this.nome = nome;
    }

    @JsonGetter("uf")
    public String getUf() {
        return uf;
    }

    @JsonSetter("SiglaEstado")
    public void setUf(String uf) {
        this.uf = uf;
    }

    @JsonGetter("vencedores")
    public String getVencedores() {
        return vencedores;
    }

    @JsonSetter("Quantidade")
    public void setVencedores(String vencedores) {
        this.vencedores = vencedores;
    }

    @JsonGetter("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonSetter("Latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonGetter("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonSetter("Longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonGetter("cidades")
    public List<Cidade> getCidades() {
        return cidades;
    }

    @JsonSetter("PremiacaoPorCidade")
    public void setCidades(List<Cidade> cidades) {
        this.cidades = cidades;
    }

    @Embeddable
    public static class Cidade {

        private String cidade;
        private String vencedores;
        private String latitude;
        private String longitude;

        @JsonGetter("cidade")
        public String getCidade() {
            return cidade;
        }

        @JsonSetter("NomeCidade")
        public void setCidade(String cidade) {
            this.cidade = cidade;
        }

        @JsonGetter("vencedores")
        public String getVencedores() {
            return vencedores;
        }

        @JsonSetter("Quantidade")
        public void setVencedores(String vencedores) {
            this.vencedores = vencedores;
        }

        @JsonGetter("latitude")
        public String getLatitude() {
            return latitude;
        }

        @JsonSetter("Latitude")
        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        @JsonGetter("longitude")
        public String getLongitude() {
            return longitude;
        }

        @JsonSetter("Longitude")
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        @Override
        public int hashCode() {
            return Objects.hash(cidade, latitude, longitude, vencedores);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Cidade)) {
                return false;
            }
            Cidade other = (Cidade) obj;
            return Objects.equals(cidade, other.cidade) && Objects.equals(latitude, other.latitude)
                    && Objects.equals(longitude, other.longitude) && Objects.equals(vencedores, other.vencedores);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, uf, vencedores, latitude, longitude, cidades);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Estado)) {
            return false;
        }
        Estado other = (Estado) obj;
        return Objects.equals(nome, other.nome) && Objects.equals(uf, other.uf)
                && Objects.equals(vencedores, other.vencedores) && Objects.equals(latitude, other.latitude)
                && Objects.equals(longitude, other.longitude) && Objects.equals(cidades, other.cidades);
    }
}
