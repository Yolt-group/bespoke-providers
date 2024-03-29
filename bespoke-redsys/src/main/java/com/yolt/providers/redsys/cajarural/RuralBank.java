package com.yolt.providers.redsys.cajarural;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RuralBank {
    BCE("BCE"),
    ORIHUELA("C.R. ORIHUELA"),
    GIJON("C.R. GIJON"),
    NAVARRA("C.R. NAVARRA"),
    EXTREMADURA("C.R. EXTREMADURA"),
    SALAMANCA("C.R. SALAMANCA"),
    SORIA("C.R. SORIA"),
    FUENTEALAMO("C.R. FUENTEALAMO"),
    UTRERA("C.R. UTRERA"),
    GRANADA("C.R. GRANADA"),
    ASTURIAS("C.R. ASTURIAS"),
    CAJAVIVA("CAJAVIVA"),
    JAEN("C.R. JAEN"),
    CAIXARURALGALEGA("CAIXA RURAL GALEGA"),
    CAJASIETE("CAJASIETE"),
    TERUEL("C.R. TERUEL"),
    ZAMORA("C.R. ZAMORA"),
    BAENA("C.R. BAENA"),
    ALCUDIA("C.R. L'ALCUDIA"),
    NUEVACARTEYA("C.R. NUEVA CARTEYA"),
    CANETEDELASTORRES("C.R. CAÑETE DE LAS TORRES"),
    SANISIDROVALLDUXO("C.R. SAN ISIDRO VALL D'UXO"),
    SANJOSEALCORA("C.R. SAN JOSE ALCORA"),
    ADAMUZ("C.R. ADAMUZ"),
    ALGEMESI("C.R. ALGEMESI"),
    CASASIBANEZ("C.R. CASAS IBAÑEZ"),
    SANJOSEDEALMASSORA("C.R. SAN JOSE DE ALMASSORA"),
    ONDA("C.R. ONDA"),
    RURALNOSTRA("RURALNOSTRA"),
    VILLAMALEA("C.R. VILLAMALEA"),
    ALBAL("C.R. ALBAL"),
    CAIXAPOPULAR("CAIXA POPULAR"),
    CAIXARURALBENICARLO("CAIXA RURAL BENICARLO"),
    LESCOVES("C.R. SAN ISIDRO LES COVES VINROMA"),
    VINAROZ("C.R. EL SALVADOR VINAROZ"),
    CAJARURALSUR("C.R. DEL SUR"),
    GLOBALCAJA("GLOBALCAJA"),
    CAJARURALARAGON("C.R. ARAGON");

    @Getter
    private final String displayName;
}
