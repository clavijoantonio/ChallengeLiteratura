package com.clavijoAntonio.challangeLiteralura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosPersona (
    @JsonAlias("birth_year")int fechaNacimiento,
    @JsonAlias("death_year")int fechaDefuncion,
    @JsonAlias("name")String nombre ){
}
