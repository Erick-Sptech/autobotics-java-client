package school.sptech;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashManutencao {
//    public static void main(String[] args) throws IOException {
//        String nomeBucketTrusted = "trusted-1d4a3f130793f4b0dfc576791dd86b37";
//        List<Captura> capturas = Gerenciador.leCsvBucketTrusted(nomeBucketTrusted);
////        // ---------------------
////// 10 que FICAM DE FORA (mais de 1h atrás)
////// ---------------------
////        capturas.add(new Captura("2025-12-02 18:50:00", 20.0, 16.0, 8.0, 500.0, 300.0, 100, "PC-001", "TechCorp", "TI", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:51:00", 21.0, 16.0, 8.1, 500.0, 301.0, 101, "PC-001", "TechCorp", "TI", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:52:00", 22.0, 16.0, 8.2, 500.0, 302.0, 102, "PC-002", "TechCorp", "RH", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:53:00", 23.0, 16.0, 8.3, 500.0, 303.0, 103, "PC-002", "TechCorp", "RH", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:54:00", 24.0, 16.0, 8.4, 500.0, 304.0, 104, "PC-003", "TechCorp", "Financeiro", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:55:00", 25.0, 16.0, 8.5, 500.0, 305.0, 105, "PC-003", "TechCorp", "Financeiro", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:56:00", 26.0, 16.0, 8.6, 500.0, 306.0, 106, "PC-004", "TechCorp", "Marketing", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:57:00", 27.0, 16.0, 8.7, 500.0, 307.0, 107, "PC-004", "TechCorp", "Marketing", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:58:00", 28.0, 16.0, 8.8, 500.0, 308.0, 108, "PC-005", "TechCorp", "Comercial", "a,b,c,d,e"));
////        capturas.add(new Captura("2025-12-02 18:59:00", 29.0, 16.0, 8.9, 500.0, 309.0, 109, "PC-005", "TechCorp", "Comercial", "a,b,c,d,e"));
////
////
////// ---------------------
////// 60 DENTRO DA ÚLTIMA HORA (20:00:00 → 20:59:00)
////// ---------------------
////        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
////
////        for (int i = 0; i < 10000; i++) {
////
////            // Timestamp dinâmico: agora - i minutos
////            String timestamp = LocalDateTime.now().minusMinutes(i).format(formatter);
////
////            // valores variados só para ficar realista
////            double cpu = 10 + (i % 50);                  // varia entre 10 e 59
////            double ramTotal = 16.0;
////            double ramUsada = 6.57 + (i % 6);             // 6 a 11
////            double discoTotal = 500.0;
////            double discoUsado = 300.0 + (i * 0.5);       // sobe devagar
////            int numProcessos = 100 + (i % 30);           // 100 a 129
////            String codigoMaquina = "Controlador-" + String.format("%03d", (i % 10) + 1);
////            String empresa = "TechCorp";
////            String setor = switch (i % 5) {
////                case 0 -> "TI";
////                case 1 -> "RH";
////                case 2 -> "Financeiro";
////                case 3 -> "Marketing";
////                default -> "Comercial";
////            };
////            String top5 = "procA,procB,procC,procD,procE";
////
////            capturas.add(new Captura(
////                    timestamp, cpu, ramTotal, ramUsada,
////                    discoTotal, discoUsado, numProcessos,
////                    codigoMaquina, empresa, setor, top5
////            ));
////        }
//
//        Gerenciador.exibeListaCapturas(capturas);
//
//        mediaCpuPorDiaDaSemana(capturas);
//        mediaRamPorDiaDaSemana(capturas);
//        picoCpuPorDiaDaSemana(capturas);
//        picoRamPorDiaDaSemana(capturas);
//
//        Map<String,Map<String, List<Double>>> mapAuxiliar = new HashMap<>();
//
//        List<Map<String,Map<String, List<Double>>>> listaInformacoes = new ArrayList<>();
//
//        mapAuxiliar.put("mediaCpu", mediaCpuPorDiaDaSemana(capturas));
//        listaInformacoes.add(mapAuxiliar);
//
//        mapAuxiliar.put("mediaRam", mediaRamPorDiaDaSemana(capturas));
//        listaInformacoes.add(mapAuxiliar);
//
//        mapAuxiliar.put("picoCpu", picoCpuPorDiaDaSemana(capturas));
//        listaInformacoes.add(mapAuxiliar);
//
//        mapAuxiliar.put("picoRam", picoRamPorDiaDaSemana(capturas));
//        listaInformacoes.add(mapAuxiliar);
//
////        listaInformacoes.add(mediaCpuPorDiaDaSemana(capturas));
////        listaInformacoes.add(mediaRamPorDiaDaSemana(capturas));
////        listaInformacoes.add(picoCpuPorDiaDaSemana(capturas));
////        listaInformacoes.add(picoRamPorDiaDaSemana(capturas));
//
////        ObjectMapper mapper = new ObjectMapper();
////        mapper.writeValue(new File("controladores.json"), listaInformacoes);
//        ObjectMapper mapper = new ObjectMapper();
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
//        mapper.writeValue(new File(LocalDateTime.now().format(formatador) + ".json"), mapAuxiliar);
//
//        String bucketName = "client-1d4a3f130793f4b0dfc576791dd86b37";
//
//        File controladoresFile = new File(LocalDateTime.now().format(formatador) + ".json");
//        try (S3Client s3 = S3Client.create()) {
//            // Upload dos registros ultimos 6 registros dos controladores
//            s3.putObject(PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key("/dashboardRoboticaNrt/jsons/"+ LocalDateTime.now().format(formatador) +".json")
//                    .build(), controladoresFile.toPath());
//            System.out.println("Arquivos enviados com sucesso para o bucket " + bucketName);
//        }
//
//    }

    public static Map<String, List<Double>> mediaCpuPorDiaDaSemana(List<Captura> lista) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.minusDays(6);

        List<Captura> capturasSemana = new ArrayList<>();
        for (Captura c : lista) {
            LocalDate data = LocalDateTime
                    .parse(c.getTimestamp(), formatter)
                    .toLocalDate();

            if (!data.isBefore(inicioSemana) && !data.isAfter(hoje)) {
                capturasSemana.add(c);
            }
        }

        Map<String, List<Captura>> porControlador = new HashMap<>();

        for (Captura c : capturasSemana) {
            String id = c.getCodigoMaquina();
            porControlador
                    .computeIfAbsent(id, k -> new ArrayList<>())
                    .add(c);
        }

        Map<String, List<Double>> mediasCpuPorControlador = new HashMap<>();

        for (String idControlador : porControlador.keySet()) {

            List<Captura> capturasControlador = porControlador.get(idControlador);

            List<Double> mediasDaSemana = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                LocalDate dia = hoje.minusDays(6 - i);

                double soma = 0.0;
                int contador = 0;

                for (Captura c : capturasControlador) {
                    LocalDate dataCaptura = LocalDateTime
                            .parse(c.getTimestamp(), formatter)
                            .toLocalDate();

                    if (dataCaptura.equals(dia)) {
                        soma += c.getCpu();
                        contador++;
                    }
                }

                double media = contador > 0 ? soma / contador : 0.0;

                mediasDaSemana.add(media);
            }

            mediasCpuPorControlador.put(idControlador, mediasDaSemana);
        }

        System.out.println(mediasCpuPorControlador);
        return mediasCpuPorControlador;
    }


    public static Map<String, List<Double>> mediaRamPorDiaDaSemana(List<Captura> lista) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.minusDays(6);

        List<Captura> capturasSemana = new ArrayList<>();
        for (Captura c : lista) {
            LocalDate data = LocalDateTime
                    .parse(c.getTimestamp(), formatter)
                    .toLocalDate();

            if (!data.isBefore(inicioSemana) && !data.isAfter(hoje)) {
                capturasSemana.add(c);
            }
        }

        Map<String, List<Captura>> porControlador = new HashMap<>();

        for (Captura c : capturasSemana) {
            String id = c.getCodigoMaquina();
            porControlador
                    .computeIfAbsent(id, k -> new ArrayList<>())
                    .add(c);
        }

        Map<String, List<Double>> mediasRamPorControlador = new HashMap<>();

        for (String idControlador : porControlador.keySet()) {

            List<Captura> capturasControlador = porControlador.get(idControlador);

            List<Double> mediasDaSemana = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                LocalDate dia = hoje.minusDays(6 - i);

                double soma = 0.0;
                int contador = 0;

                for (Captura c : capturasControlador) {
                    LocalDate dataCaptura = LocalDateTime
                            .parse(c.getTimestamp(), formatter)
                            .toLocalDate();

                    if (dataCaptura.equals(dia)) {
                        soma += c.getRamUsada();
                        contador++;
                    }
                }

                double media = contador > 0 ? soma / contador : 0.0;

                mediasDaSemana.add(media);
            }

            mediasRamPorControlador.put(idControlador, mediasDaSemana);
        }

        System.out.println(mediasRamPorControlador);
        return mediasRamPorControlador;
    }

    public static Map<String, List<Double>> picoCpuPorDiaDaSemana(List<Captura> lista){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.minusDays(6);

        List<Captura> capturasSemana = new ArrayList<>();
        for (Captura c : lista) {
            LocalDate data = LocalDateTime
                    .parse(c.getTimestamp(), formatter)
                    .toLocalDate();

            if (!data.isBefore(inicioSemana) && !data.isAfter(hoje)) {
                capturasSemana.add(c);
            }
        }

        Map<String, List<Captura>> porControlador = new HashMap<>();

        for (Captura c : capturasSemana) {
            String id = c.getCodigoMaquina();
            porControlador
                    .computeIfAbsent(id, k -> new ArrayList<>())
                    .add(c);
        }

        Map<String, List<Double>> picoCpuPorControlador = new HashMap<>();

        for (String idControlador : porControlador.keySet()) {

            List<Captura> capturasControlador = porControlador.get(idControlador);

            List<Double> picosDaSemana = new ArrayList<>();


            for (int i = 0; i < 7; i++) {
                LocalDate dia = hoje.minusDays(6 - i);

                Double picoDoDia = 0.0;

                for (Captura c : capturasControlador) {
                    LocalDate dataCaptura = LocalDateTime
                            .parse(c.getTimestamp(), formatter)
                            .toLocalDate();

                    if (dataCaptura.equals(dia)) {
                        if (c.getCpu() > picoDoDia){
                            picoDoDia = c.getCpu();
                        }
                    }
                }

                picosDaSemana.add(picoDoDia);

            }

            picoCpuPorControlador.put(idControlador, picosDaSemana);
        }

        System.out.println(picoCpuPorControlador);
        return picoCpuPorControlador;
    }

    public static Map<String, List<Double>> picoRamPorDiaDaSemana(List<Captura> lista){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.minusDays(6);

        List<Captura> capturasSemana = new ArrayList<>();
        for (Captura c : lista) {
            LocalDate data = LocalDateTime
                    .parse(c.getTimestamp(), formatter)
                    .toLocalDate();

            if (!data.isBefore(inicioSemana) && !data.isAfter(hoje)) {
                capturasSemana.add(c);
            }
        }

        Map<String, List<Captura>> porControlador = new HashMap<>();

        for (Captura c : capturasSemana) {
            String id = c.getCodigoMaquina();
            porControlador
                    .computeIfAbsent(id, k -> new ArrayList<>())
                    .add(c);
        }

        Map<String, List<Double>> picoRamPorControlador = new HashMap<>();

        for (String idControlador : porControlador.keySet()) {

            List<Captura> capturasControlador = porControlador.get(idControlador);

            List<Double> picosDaSemana = new ArrayList<>();


            for (int i = 0; i < 7; i++) {
                LocalDate dia = hoje.minusDays(6 - i);

                Double picoDoDia = 0.0;

                for (Captura c : capturasControlador) {
                    LocalDate dataCaptura = LocalDateTime
                            .parse(c.getTimestamp(), formatter)
                            .toLocalDate();

                    if (dataCaptura.equals(dia)) {
                        if (c.getRamUsada() > picoDoDia){
                            picoDoDia = c.getRamUsada();
                        }
                    }
                }

                picosDaSemana.add(picoDoDia);

            }

            picoRamPorControlador.put(idControlador, picosDaSemana);
        }

        System.out.println(picoRamPorControlador);
        return picoRamPorControlador;
    }

}
