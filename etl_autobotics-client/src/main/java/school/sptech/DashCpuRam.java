package school.sptech;

import com.google.protobuf.MapEntry;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DashCpuRam {
//    public static void main(String[] args) {
//        List<Captura> capturas = new ArrayList<>();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        for (int i = 0; i < 10000; i++) {
//
//            // Timestamp dinâmico: agora - i minutos
//            String timestamp = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).minusSeconds(i * 10).format(formatter);
//
//            // valores variados só para ficar realista
//            double cpu = 10 + (i % 50);                  // varia entre 10 e 59
//            double ramTotal = 16.0;
//            double ramUsada = 6.0 + (i % 6);             // 6 a 11
//            double discoTotal = 500.0;
//            double discoUsado = 300.0 + (i * 0.5);       // sobe devagar
//            int numProcessos = 100 + (i % 30);           // 100 a 129
//            String codigoMaquina = "PC-" + String.format("%03d", (i % 10) + 1);
//            String empresa = "TechCorp";
//            String setor = switch (i % 5) {
//                case 0 -> "TI";
//                case 1 -> "RH";
//                case 2 -> "Financeiro";
//                case 3 -> "Marketing";
//                default -> "Comercial";
//            };
//            String top5 = "procA,procB,procC,procD,procE";
//
//            capturas.add(new Captura(
//                    timestamp, cpu, ramTotal, ramUsada,
//                    discoTotal, discoUsado, numProcessos,
//                    codigoMaquina, empresa, setor, top5
//            ));
//        }
//        getDadosUltimaHora(capturas);
//    }

    public static void exibirCapturasControlador(List<Captura> lista, String controlador) {
        for (Captura c : lista) {
            if (c.getCodigoMaquina().equals(controlador)) {
                System.out.println(c);
            }
        }
    }

    public static List<Captura> getCapturasControlador(List<Captura> lista, String controlador) {
        List<Captura> resultado = new ArrayList<>();
        for (Captura c : lista) {
            if (c.getCodigoMaquina().equals(controlador)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public static List<String> getListaControladores(List<Captura> lista) {
        List<String> resultado = new ArrayList<>();
        System.out.println("Listando controladores...");
        for (Captura c : lista) {
            if (!resultado.contains(c.getCodigoMaquina())) {
                resultado.add(c.getCodigoMaquina());
                System.out.println("Controlador identificado: " + c.getCodigoMaquina());
            }
        }

        return resultado;
    }


    //
//    public static void getDadosUltimaHora(List<Captura> lista) {
//        List<Captura> capturasUltimaHora = new ArrayList<>();
//        Integer tamanhoLista = lista.size() - 1;
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        LocalDateTime periodo1Hora = LocalDateTime.parse(
//                LocalDateTime.now().format(formatador),
//                formatador
//        );
//
//        periodo1Hora = periodo1Hora.minusHours(1);
//
//        while (true) {
//            LocalDateTime dataCaptura = LocalDateTime.parse(lista.get(tamanhoLista).getTimestamp(), formatador);
//            if (dataCaptura.isAfter(periodo1Hora)) {
//                capturasUltimaHora.add(lista.get(tamanhoLista));
//            }
//            else {
//                break;
//            }
//        }
//    }
//}
    public static List<Captura> getDadosUltimaHora(List<Captura> lista) {
        List<Captura> capturasUltimaHora = new ArrayList<>();
        Integer index = lista.size() - 1;
        lista = lista.reversed();

        DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZoneId zona = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime maiorData = ZonedDateTime.now(zona);
        System.out.println("Pegandos dados a partir de :" + maiorData.minusHours(1));


        ZonedDateTime periodo1Hora = maiorData.minusHours(1);

        while (true) {
            ZonedDateTime dataCaptura = LocalDateTime.parse(lista.get(index).getTimestamp(), formatadorHora).atZone(zona);
            if (dataCaptura.isAfter(periodo1Hora) || dataCaptura.isEqual(periodo1Hora)) {
                capturasUltimaHora.add(lista.get(index));
                System.out.println(dataCaptura + " veio depois de " + periodo1Hora + ".");
            } else {
                System.out.println(dataCaptura + " veio antes de " + periodo1Hora + ".");
                break;
            }
            index--;
        }

        return capturasUltimaHora;
    }

    public static Map<String, Map<String, String>> getMediaCpuRam(List<Captura> lista) {
        Map<String, Map<String, String>> resultado = new HashMap<>();

        Double somaCpu = 0.0;
        Double somaRam = 0.0;
        Double totalRam = lista.getFirst().getRamTotal();

        for (Captura c : lista) {
            somaRam += c.getRamUsada();
            somaCpu += c.getCpu();
        }

        Map<String, String> medias = new HashMap<>();
        medias.put("cpu", Double.isNaN(somaCpu/lista.size()) ? "0.0" : String.format("%.1f", somaCpu/lista.size()).replace(",", "."));
        medias.put("ram", Double.isNaN(somaRam/lista.size() / totalRam * 100) ? "0.0" : String.format("%.1f", somaRam/lista.size()).replace(",", "."));
        resultado.put("medias", medias);
        System.out.println("TESTE MAP: " + medias.get("cpu"));

        Map<String, String> metricas = new HashMap<>();
        metricas.put("ramTotal", Double.isNaN(totalRam) ? "0.0" : String.format("%.1f", lista.getFirst().getRamTotal()).replace(",", "."));
        metricas.put("ramUsada", Double.isNaN((somaRam / lista.size()) / 100 * lista.getFirst().getRamTotal()) ? "0.0" : String.format("%.1f", somaRam / lista.size() / 100 * lista.getFirst().getRamTotal()).replace(",", "."));
        resultado.put("metricas", metricas);

        System.out.println("Média de CPU: " + resultado.get("medias").get("cpu") + "\nMédia de RAM: " + resultado.get("medias").get("ram"));

        return resultado;
    }

    public static Map<String, Map<String, String>> getMediaCpuRamCada5Minutos(List<Captura> lista) {
        ZoneId zona = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime horaAtual = ZonedDateTime.now(zona);
        Map<String, Map<String, String>> resultado = new LinkedHashMap<>();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatadorJson = DateTimeFormatter.ofPattern("HH:mm");
        Double somaCpu;
        Double somaRam;
        Integer contador = 0;

        for(int i = 0; i < 12; i++) {
            Map<String, String> linhaResultado = new HashMap<>();
            somaCpu = 0.0;
            somaRam = 0.0;

            for (Captura c : lista) {
                LocalDateTime horaCap = LocalDateTime.parse(c.getTimestamp(), formatador);
                ZonedDateTime horaCapFormt = horaCap.atZone(zona);
                if (horaCapFormt.isAfter(horaAtual.minusMinutes((i * 5) + 5)) &&
                        horaCapFormt.isBefore(horaAtual.minusMinutes(i * 5))) {
                    somaCpu += c.getCpu();
                    somaRam += c.getRamUsada();
                    contador ++;
                }
            }
            System.out.println(Double.toString(somaCpu/contador));

            linhaResultado.put("cpu", Double.isNaN(somaCpu/contador) ? "0.0" : String.format("%.1f", somaCpu / contador).replace(",", "."));
            linhaResultado.put("ram", Double.isNaN(somaCpu/contador) ? "0.0" : String.format("%.1f", somaRam / contador).replace(",", "."));
            contador = 0;

            resultado.put(horaAtual.minusMinutes(i * 5).format(formatadorJson), linhaResultado);
        }

        for (Map.Entry<String, Map<String, String>> entry : resultado.entrySet()) {
            System.out.println("Média das " + entry.getKey() + " : " + entry.getValue());
        }

        return resultado;
    }

    public static Map<String, Map<String, String>> getPicosRamCpu (Map<String, Map<String, String>> listaMap) {
        Map<String, Map<String, String>> resultado = new HashMap<>();
        Double maiorCpu = Double.NEGATIVE_INFINITY;
        Double maiorRam = Double.NEGATIVE_INFINITY;
        String timestampCpu = "";
        String timestampRam = "";

        for (Map.Entry <String, Map<String, String>> entry : listaMap.entrySet()) {
            if (Double.parseDouble(entry.getValue().get("cpu")) > maiorCpu) {
                maiorCpu = Double.parseDouble(entry.getValue().get("cpu"));
                timestampCpu = entry.getKey();
            }
            if (Double.parseDouble(entry.getValue().get("ram")) > maiorRam) {
                maiorRam = Double.parseDouble(entry.getValue().get("ram"));
                timestampRam = entry.getKey();
            }
        }
        Map<String, String> mapCpu = new HashMap<>();
        mapCpu.put("valor", Double.isNaN(maiorCpu) ? "0.0" : String.format("%.1f", maiorCpu).replace(",", "."));
        mapCpu.put("timestamp", timestampCpu);

        Map<String, String> mapRam = new HashMap<>();
        mapRam.put("valor", Double.isNaN(maiorRam) ? "0.0" : String.format("%.1f", maiorRam).replace(",", "."));
        mapRam.put("timestamp", timestampRam);

        resultado.put("cpu", mapCpu);
        resultado.put("ram", mapRam);

        System.out.println("Pico de CPU: " + resultado.get("cpu").get("valor") + " ás " + resultado.get("cpu").get("timestamp"));
        System.out.println("Pico de RAM: " + resultado.get("ram").get("valor") + " ás " + resultado.get("ram").get("timestamp"));

        return resultado;
    }

    public static Map<String, Map<String, String>> getUltimasCapturasCpuRam (List<Captura> lista) {
        Map<String, Map<String, String>> resultado = new HashMap<>();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatadorJson = DateTimeFormatter.ofPattern("HH:mm");
        Captura ultimaCaptura = lista.getLast();

        Map<String, String> cpuAux = new HashMap<>();
        cpuAux.put("valor", Double.isNaN(ultimaCaptura.getCpu()) ? "0.0" : String.format("%.1f", ultimaCaptura.getCpu()).replace(",", "."));
        cpuAux.put("timestamp", LocalDateTime.parse(ultimaCaptura.getTimestamp(), formatador).format(formatadorJson));

        Map<String, String> ramAux = new HashMap<>();
        ramAux.put("valor", Double.isNaN(ultimaCaptura.getRamUsada()) ? "0.0" : String.format("%.1f", ultimaCaptura.getRamUsada()).replace(",", "."));
        ramAux.put("timestamp", LocalDateTime.parse(ultimaCaptura.getTimestamp(), formatador).format(formatadorJson));

        resultado.put("cpu", cpuAux);
        resultado.put("ram", ramAux);

        System.out.println("Última captura CPU: " + resultado.get("cpu").get("valor") + " ás " + resultado.get("cpu").get("timestamp"));
        System.out.println("Última captura RAM: " + resultado.get("ram").get("valor") + " ás " + resultado.get("ram").get("timestamp"));

        return resultado;
    }

    public static Map<String, Map<String, String>> getProcessos (List<Captura> capturas) {
        Map<String, Map<String, String>> resultado = new HashMap<>();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatadorJson = DateTimeFormatter.ofPattern("HH:mm");

        for(Captura c : capturas) {
            Map<String, String> mapAux = new HashMap<>();
            mapAux.put("topProcessos", c.getTop5Processos());
            mapAux.put("timestamp", LocalDateTime.parse(c.getTimestamp(), formatador).format(formatadorJson));
            resultado.put("processos", mapAux);
        }
        Map<String, String> mapAux = new HashMap<>();
        mapAux.put("totalProcessos", capturas.getFirst().getNumProcessos().toString());
        resultado.put("totalProcessos", mapAux);
        return resultado;
    }

    public static Map<String, Map<String, Map<String, Map<String, String>>>> criarJsonCpuRam (List<Captura> capturas) {

        Map<String, Map<String, Map<String, Map<String, String>>>> mapperResultado = new HashMap<>();
        List<String> controladores = getListaControladores(capturas);

        for (String c : controladores) {
            Map<String, Map<String, Map<String, String>>> mapAux = new HashMap<>();

            mapAux.put("media5Minutos" ,getMediaCpuRamCada5Minutos(getDadosUltimaHora((getCapturasControlador(capturas, c)))));
            mapAux.put("picoCpuRam", getPicosRamCpu(getMediaCpuRamCada5Minutos(getDadosUltimaHora(getCapturasControlador(capturas, c)))));
            mapAux.put("ultimasCapturas", getUltimasCapturasCpuRam(getDadosUltimaHora(getCapturasControlador(capturas, c))));
            mapAux.put("mediaCpuRam", getMediaCpuRam(getDadosUltimaHora(getCapturasControlador(capturas, c))));
            mapAux.put("topProcessos", getProcessos(getDadosUltimaHora(getCapturasControlador(capturas, c))));
            mapperResultado.put(c, mapAux);
        }

        return mapperResultado;
    }
}