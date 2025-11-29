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

                String mediaFormatada = String.format("%.2f", media).replace(",", ".");

                media = Double.parseDouble(mediaFormatada);

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

                String mediaFormatada = String.format("%.2f", media).replace(",", ".");

                media = Double.parseDouble(mediaFormatada);

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

    public static Map<String, Map<String, List<Double>>> criarJsonManutencao(List<Captura> capturas){
        //Gerenciador.exibeListaCapturas(capturas);

        DashManutencao.mediaRamPorDiaDaSemana(capturas);
        DashManutencao.mediaRamPorDiaDaSemana(capturas);
        DashManutencao.picoCpuPorDiaDaSemana(capturas);
        DashManutencao.picoRamPorDiaDaSemana(capturas);

        Map<String, Map<String, List<Double>>> mapAuxiliar = new HashMap<>();

        List<Map<String,Map<String, List<Double>>>> listaInformacoes = new ArrayList<>();

        mapAuxiliar.put("mediaCpu", DashManutencao.mediaCpuPorDiaDaSemana(capturas));
        listaInformacoes.add(mapAuxiliar);

        mapAuxiliar.put("mediaRam", DashManutencao.mediaRamPorDiaDaSemana(capturas));
        listaInformacoes.add(mapAuxiliar);

        mapAuxiliar.put("picoCpu", DashManutencao.picoCpuPorDiaDaSemana(capturas));
        listaInformacoes.add(mapAuxiliar);

        mapAuxiliar.put("picoRam", DashManutencao.picoRamPorDiaDaSemana(capturas));
        listaInformacoes.add(mapAuxiliar);

        return mapAuxiliar;
    }

}