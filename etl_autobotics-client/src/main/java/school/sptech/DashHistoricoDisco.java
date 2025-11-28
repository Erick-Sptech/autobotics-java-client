package school.sptech;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class DashHistoricoDisco {
//    public static void main(String[] args) {
//        //List<Captura> lista = Gerenciador.leCsvBucketTrusted("trusted-1d4a3f130793f4b0dfc576791dd86b32");
//        //List<Captura> capturas = new ArrayList<>();
//        String nomeBucketTrusted = "trusted-1d4a3f130793f4b0dfc576791dd86b31";
//        List<Captura> capturas = Gerenciador.leCsvBucketTrusted(nomeBucketTrusted);
//
//        executarEtlDisco(capturas);
//    }

    public static void executarEtlDisco(List<Captura> capturas){
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String nomeBucketClient = "client-1d4a3f130793f4b0dfc576791dd86b32";
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = LocalDate.now();

        //Gerenciador.exibeListaCapturas(capturas);

        //exibirCapturasControlador(capturas, "PC-009");
        Map<Integer, List<Double>> mediasSetor = mediaDiscoHoje(capturas);
        Map<String, Double> c = new HashMap<>();
        Map<String, Integer> idSetor = new HashMap<>();
        Map<String, LinkedHashMap<List<Double>, List<LocalDate>>> mediasGrafico = calcularMediaUltimosMeses(capturas);
        //System.out.println(mediasGrafico);

        List<ResultadoHistoricoDisco> resultadoJson = new ArrayList<>();


        for (Map.Entry<String, LinkedHashMap<List<Double>, List<LocalDate>>> capturasDoControlador : mediasGrafico.entrySet()){
            String codigoControlador = capturasDoControlador.getKey();
            LinkedHashMap<List<Double>, List<LocalDate>> mediasEdatas = capturasDoControlador.getValue();
            idSetor.put(codigoControlador, buscarSetorID(codigoControlador));
            Map.Entry<List<Double>, List<LocalDate>> entrada = mediasEdatas.entrySet().iterator().next();

            List<Double> medias = entrada.getKey();
            List<LocalDate> datas = entrada.getValue();
            Integer Setor = idSetor.get(codigoControlador);

            LocalDate primeiroDia = datas.get(0);
            List<Double> datasDouble = new ArrayList<>();
            for (LocalDate d : datas) {
                datasDouble.add((double) ChronoUnit.DAYS.between(primeiroDia, d));
            }

            List<Double> coeficientes = calculaLinear(medias, datasDouble);
            Double angular = coeficientes.get(0);
            Double linear = coeficientes.get(1);

            // parte adaptar começo
            c.put(codigoControlador, buscarCritico(codigoControlador));
            Double diaDoCritico = (c.get(codigoControlador) - linear) / angular;

            Double ultimoX = datasDouble.getLast();
            if (diaDoCritico <= ultimoX) {
                //System.out.println("O crítico já foi ultrapassado");
            }

            LocalDate dataCritica = primeiroDia.plusDays((long) Math.floor(diaDoCritico));
            //Double yCriticoEstimado = angular * diaDoCritico + linear;

            Long diasFaltam = ChronoUnit.DAYS.between(hoje, dataCritica);
            String quantoFalta = "";


            if (diasFaltam < 0){
                quantoFalta = "Já ultrapassou o crítico faz "+ diasFaltam*-1 + " dias";
            } else if(diasFaltam == 0){
                quantoFalta = "Atinge o crítico hoje";
            } else {
                quantoFalta = "faltam " + diasFaltam + " dias";
            }
            // parte adaptar final

            System.out.println("Controlador: " + codigoControlador);
            System.out.println("Coeficientes: " + coeficientes);
            System.out.println("Datas: " + datas);
            System.out.println("datas double: " + datasDouble);
            System.out.println("dia do critico: " + dataCritica);
            System.out.println("quanto falta string: " + quantoFalta);
            System.out.println("media: " + medias + "\n");

            resultadoJson.add(new ResultadoHistoricoDisco(codigoControlador, datas, medias, coeficientes, mediasSetor, dataCritica, quantoFalta, Setor));
        }

        String nomeArquivo = agora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + "_historico_disco" + ".json";

        try{
            String json = mapper.writeValueAsString(resultadoJson);
            mapper.writeValue(new File(nomeArquivo), resultadoJson);
            System.out.println(json);
        }catch (JsonProcessingException e){
            System.out.println("erro ao processar o json");
        }catch (IOException e){
            System.out.println("erro de entrada e saida");
        }

        File controladoresFile = new File(nomeArquivo);
        try (S3Client s3 = S3Client.create()) {
            s3.putObject(PutObjectRequest.builder()
                    .bucket(nomeBucketClient)
                    .key("dashboard_historico_disco/jsons/"+ nomeArquivo)
                    .build(), controladoresFile.toPath());
            System.out.println("Arquivos enviados com sucesso para o bucket " + nomeBucketClient);
        }
    }

    public static void exibirCapturasControlador(List<Captura> lista, String controlador) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Captura c : lista) {
            LocalDateTime dataHora = LocalDateTime.parse(c.getTimestamp(), formatter);
            if (c.getCodigoMaquina().equals(controlador)) {
                System.out.println(c);
            }
        }
    }


    public static Map<Integer, List<Double>> mediaDiscoHoje(List<Captura> lista) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDate ontem = LocalDate.now().minusDays(1);
        LocalDate amanha = LocalDate.now().plusDays(1);

        List<Captura> capturasHoje = new ArrayList<>();
        for (Captura c : lista) {
            LocalDate data = LocalDateTime
                    .parse(c.getTimestamp(), formatter)
                    .toLocalDate();

            if (data.isAfter(ontem) && data.isBefore(amanha)) {
                capturasHoje.add(c);
            }
        }

        Map<Integer, List<Captura>> porSetor = new HashMap<>();

        for (Captura c : capturasHoje) {
            Integer setorID = buscarSetorID(c.getCodigoMaquina());
            if (!porSetor.containsKey(setorID)) {
                porSetor.put(setorID, new ArrayList<>());
            }
            porSetor.get(setorID).add(c);

        }

        Map<Integer, List<Double>> mediasDiscoPorSetor = new HashMap<>();

        for (Integer setorNome : porSetor.keySet()) {

            List<Captura> capturasSetor = porSetor.get(setorNome);

            List<Double> mediasDosControladores = new ArrayList<>();

            Double soma = 0.0;
            int contador = 0;

            for (Captura c : capturasSetor) {
                soma += c.getDiscoUsado();
                contador++;
            }

            Double media = contador > 0 ? soma / contador : 0.0;
            String resultado = String.format("%.2f", media).replace(",", ".");
            media = Double.parseDouble(resultado);

            mediasDosControladores.add(media);

            mediasDiscoPorSetor.put(setorNome, mediasDosControladores);
        }
        return mediasDiscoPorSetor;
    }

    public static Map<String, LinkedHashMap<List<Double>, List<LocalDate>>> calcularMediaUltimosMeses(List<Captura> lista) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate hoje = LocalDate.now();

        List<YearMonth> meses = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            YearMonth ym = YearMonth.from(hoje.minusMonths(6 - i));
            meses.add(ym);
        }

        Map<String, List<Captura>> porControlador = new HashMap<>();
        for (Captura c : lista) {
            String id = c.getCodigoMaquina();

            if (!porControlador.containsKey(id)) {
                porControlador.put(id, new ArrayList<>());
            }
            porControlador.get(id).add(c);
        }

        Map<String, LinkedHashMap<List<Double>, List<LocalDate>>> mediasDatasPorControlador = new HashMap<>();

        for (Map.Entry<String, List<Captura>> capturasDoControlador : porControlador.entrySet()) {
            String idControlador = capturasDoControlador.getKey();
            List<Captura> capturasControlador = capturasDoControlador.getValue();

            List<Double> medias = new ArrayList<>();
            List<LocalDate> ultimasDatas = new ArrayList<>();

            for (YearMonth ym : meses) {
                LocalDate inicioMes = ym.atDay(1);
                LocalDate fimMes = ym.atEndOfMonth();

                Double soma = 0.0;
                int contador = 0;
                LocalDate ultima = null;

                for (Captura c : capturasControlador) {
                    LocalDate dataCaptura = LocalDateTime.parse(c.getTimestamp(), formatter).toLocalDate();

                    if (!dataCaptura.isBefore(inicioMes) && !dataCaptura.isAfter(fimMes)) {
                        soma += c.getDiscoUsado();
                        contador++;
                        if (ultima == null || dataCaptura.isAfter(ultima)) {
                            ultima = dataCaptura;
                        }
                    }
                }

                double media = contador > 0 ? soma / contador : 0.0;
                String resultado = String.format("%.2f", media).replace(",", ".");
                media = Double.parseDouble(resultado);

                medias.add(media);
                ultimasDatas.add(ultima);
            }

            LinkedHashMap<List<Double>, List<LocalDate>> mediasEDatas = new LinkedHashMap<>();
            mediasEDatas.put(Collections.unmodifiableList(medias), Collections.unmodifiableList(ultimasDatas));
            mediasDatasPorControlador.put(idControlador, mediasEDatas);
        }
        return mediasDatasPorControlador;
    }

    public static List<Double> calculaLinear(List<Double> usoDeDisco, List<Double> diasGrafico) {
        if (usoDeDisco.size() != diasGrafico.size()) {
            return List.of(0.0, 0.0);
        }

        int n = usoDeDisco.size();
        Double x1 = 0.0;
        Double y1 = 0.0;
        Double xy = 0.0;
        Double x2 = 0.0;

        for (int i = 0; i < n; i++) {
            Double xi = diasGrafico.get(i);
            Double yi = usoDeDisco.get(i);
            x1 += xi;
            y1 += yi;
            xy += xi * yi;
            x2 += xi * xi;
        }

        Double J = (n * x2) - (x1 * x1);
        if (J == 0.0) {
            return List.of(0.0, 0.0);
        }
        Double angular = ((n * xy) - (x1 * y1)) / J;
        Double linear = (y1 - angular * x1) / n;


        return List.of(angular, linear);
    }

    public static Double buscarCritico(String codigo){
        Connection con = new Connection();
        JdbcTemplate template = con.getTemplate();

        String sql = "select p.valor from parametro p \n" +
                "inner join componente c\n" +
                "\ton c.id_componente = p.fk_componente\n" +
                "inner join setor s\n" +
                "\ton s.id_setor = c.fk_setor\n" +
                "inner join controlador ctr\n" +
                "\ton s.id_setor = ctr.fk_setor\n" +
                "where ctr.numero_serial = ? and criticidade = 2\n" +
                "and c.nome = \"Disco\";";

        Double critico = null;

        try {
            if (codigo != null){
                critico = template.queryForObject(sql, Double.class, codigo);
            }else {
                critico = 100.0;
            }
        }catch (EmptyResultDataAccessException e){
            System.out.println("não encontrei critico");
        }

        return critico;
    }

    public static Integer buscarSetorID(String codigo){
        Connection con = new Connection();
        JdbcTemplate template = con.getTemplate();

        String sql = "select s.id_setor\n" +
                "from setor s\n" +
                "inner join controlador c\n" +
                "\ton s.id_setor = c.fk_setor\n" +
                "where c.numero_serial = ?;";

        Integer setor = null;

        try{
            if (codigo != null){
                setor = template.queryForObject(sql, Integer.class, codigo);
            } else{
                setor = null;
            }
        }catch (EmptyResultDataAccessException e){
            System.out.println("codigo sem setor");
        }
        return setor;
    }



}
