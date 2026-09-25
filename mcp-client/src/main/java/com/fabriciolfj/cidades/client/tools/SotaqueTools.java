package com.fabriciolfj.cidades.client.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Tool LOCAL do client (@Tool do Spring AI): roda dentro deste processo,
 * sem MCP. Os dados são uma lista fixa, sem banco.
 */
@Component
public class SotaqueTools {

    public record Sotaque(String nome, List<String> ufs, String caracteristica) {
    }

    private static final List<Sotaque> SOTAQUES = List.of(
            new Sotaque("Caipira", List.of("SP", "MG", "GO", "MS", "PR"),
                    "'R' retroflexo (porrrta), comum no interior"),
            new Sotaque("Paulistano", List.of("SP"),
                    "'E' fechado e 'R' mais suave, 'meu' como vocativo"),
            new Sotaque("Carioca", List.of("RJ"),
                    "'S' chiado (xh) e 'R' aspirado"),
            new Sotaque("Mineiro", List.of("MG"),
                    "Junção e corte de sílabas ('pó pô' = pode pôr), 'uai' e 'trem'"),
            new Sotaque("Gaúcho", List.of("RS"),
                    "Uso de 'tu' com verbo na 3ª pessoa, 'bah', 'tchê'"),
            new Sotaque("Nordestino", List.of("BA", "SE", "AL", "PE", "PB", "RN", "CE", "PI", "MA"),
                    "Vogais abertas ('pÉrnambuco') e 'T'/'D' não palatalizados ('tia' sem 'tchia')"),
            new Sotaque("Nortista", List.of("AM", "PA", "AC", "RO", "RR", "AP", "TO"),
                    "'S' chiado parecido com o carioca e vocabulário com influência indígena"),
            new Sotaque("Brasiliense", List.of("DF"),
                    "Sotaque 'neutro', mistura de migrantes de todo o país"),
            new Sotaque("Manezinho", List.of("SC"),
                    "Fala rápida de Florianópolis com influência açoriana"));

    @Tool(name = "listar_sotaques", description = "Lista todos os sotaques brasileiros conhecidos e as UFs onde ocorrem")
    public List<Sotaque> listarSotaques() {
        return SOTAQUES;
    }

    @Tool(name = "sotaques_por_uf", description = "Retorna os sotaques falados em um estado (UF)")
    public List<Sotaque> sotaquesPorUf(
            @ToolParam(description = "Sigla do estado com 2 letras, ex: SP, RJ, MG") String uf) {
        var sigla = uf.trim().toUpperCase(Locale.ROOT);
        return SOTAQUES.stream().filter(s -> s.ufs().contains(sigla)).toList();
    }
}
