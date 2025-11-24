package school.sptech;

import com.google.protobuf.MapEntry;

import java.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DashCpuRam {
//    public static void main(String[] args) {
////        List<Captura> lista = Gerenciador.leCsvBucketTrusted("trusted-1d4a3f130793f4b0dfc576791dd86b32");
//        List<Captura> capturas = new ArrayList<>();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        for (int i = 0; i < 10000; i++) {
//
//            // Timestamp dinâmico: agora - i minutos
//            String timestamp = LocalDateTime.now().minusSeconds(i * 10).format(formatter);
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
//        Gerenciador.exibeListaCapturas(capturas);
//        Collections.reverse(capturas);
//        exibirCapturasControlador(capturas, "0001");
//        getMediaCpuRam(getDadosUltimaHora(capturas));
//        Gerenciador.exibeListaCapturas(getDadosUltimaHora(capturas));
//        getMediaCpuRamCada5Minutos(getDadosUltimaHora(capturas));
//        getPicosRamCpu(getMediaCpuRamCada5Minutos(getDadosUltimaHora(capturas)));
//        getUltimasCapturasCpuRam(capturas);
//    }

    public static void exibirCapturasControlador(List<Captura> lista, String controlador) {
        for (Captura c : lista) {
            if (c.getCodigoMaquina().equals(controlador)) {
                System.out.println(c);
            }
        }
    }

    public static void getCapturasControlador(List<Captura> lista, String controlador) {

        for (Captura c : lista) {
            if (c.getCodigoMaquina().equals(controlador)) {

            }
        }
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

        DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime maiorData = LocalDateTime.parse(lista.get(index).getTimestamp(), formatadorHora);


        LocalDateTime periodo1Hora = maiorData.minusHours(1);

        if (LocalDateTime.parse(lista.getFirst().getTimestamp(), formatadorHora).isAfter(periodo1Hora)
                || LocalDateTime.parse(lista.getFirst().getTimestamp(), formatadorHora).isEqual(periodo1Hora)) {
            return lista;
        }

        while (true) {
            LocalDateTime dataCaptura = LocalDateTime.parse(lista.get(index).getTimestamp(), formatadorHora);
            System.out.println("Comparando se " + dataCaptura + " veio depois de " + periodo1Hora);
            if (dataCaptura.isAfter(periodo1Hora) || dataCaptura.isEqual(periodo1Hora)) {
//                System.out.println(dataCaptura + " certo");
                capturasUltimaHora.add(lista.get(index));
            } else {
                System.out.println(dataCaptura + " veio antes de " + periodo1Hora + ".");
                break;
            }
            index--;
        }

        return capturasUltimaHora;
    }

    public static Map<String, Double> getMediaCpuRam(List<Captura> lista) {
        Double somaCpu = 0.0;
        Double somaRam = 0.0;

        for (Captura c : lista) {
            somaRam += c.getRamUsada();
            somaCpu += c.getCpu();
        }

        Map<String, Double> resultado = new HashMap<>();
        resultado.put("cpu", somaCpu/lista.size());
        resultado.put("ram", somaRam/lista.size());

        System.out.println("Média de CPU: " + resultado.get("cpu") + "\nMédia de RAM: " + resultado.get("ram"));

        return resultado;
    }

    public static Map<String, Map<String, String>> getMediaCpuRamCada5Minutos(List<Captura> lista) {
        LocalDateTime horaAtual = LocalDateTime.now();
        Map<String, Map<String, String>> resultado = new LinkedHashMap<>();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Double somaCpu;
        Double somaRam;
        Integer contador = 0;

        for(int i = 0; i < 12; i++) {
            Map<String, String> linhaResultado = new HashMap<>();
            somaCpu = 0.0;
            somaRam = 0.0;

            for (Captura c : lista) {
                if (LocalDateTime.parse(c.getTimestamp(), formatador).isAfter(horaAtual.minusMinutes((i * 5) + 5)) &&
                        LocalDateTime.parse(c.getTimestamp(), formatador).isBefore(horaAtual.minusMinutes(i * 5))) {
                    somaCpu += c.getCpu();
                    somaRam += c.getRamUsada();
                    contador ++;
                }
            }
            System.out.println(Double.toString(somaCpu/contador));

            linhaResultado.put("cpu", Double.toString(somaCpu/contador));
            linhaResultado.put("ram", Double.toString(somaRam/contador));
            contador = 0;

            resultado.put(horaAtual.minusMinutes(i * 5).format(formatador), linhaResultado);
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
        mapCpu.put("valor", Double.toString(maiorCpu));
        mapCpu.put("timestamp", timestampCpu);

        Map<String, String> mapRam = new HashMap<>();
        mapRam.put("valor", Double.toString(maiorRam));
        mapRam.put("timestamp", timestampRam);

        resultado.put("cpu", mapCpu);
        resultado.put("ram", mapRam);

        System.out.println("Pico de CPU: " + resultado.get("cpu").get("valor") + " ás " + resultado.get("cpu").get("timestamp"));
        System.out.println("Pico de RAM: " + resultado.get("ram").get("valor") + " ás " + resultado.get("ram").get("timestamp"));

        return resultado;
    }

    public static Map<String, Map<String, String>> getUltimasCapturasCpuRam (List<Captura> lista) {
        Map<String, Map<String, String>> resultado = new HashMap<>();
        Captura ultimaCaptura = lista.getLast();

        Map<String, String> cpuAux = new HashMap<>();
        cpuAux.put("valor", Double.toString(ultimaCaptura.getCpu()));
        cpuAux.put("timestamp", ultimaCaptura.getTimestamp());

        Map<String, String> ramAux = new HashMap<>();
        ramAux.put("valor", Double.toString(ultimaCaptura.getRamUsada()));
        ramAux.put("timestamp", ultimaCaptura.getTimestamp());

        resultado.put("cpu", cpuAux);
        resultado.put("ram", ramAux);

        System.out.println("Última captura CPU: " + resultado.get("cpu").get("valor") + " ás " + resultado.get("cpu").get("timestamp"));
        System.out.println("Última captura RAM: " + resultado.get("ram").get("valor") + " ás " + resultado.get("ram").get("timestamp"));

        return resultado;
    }
}