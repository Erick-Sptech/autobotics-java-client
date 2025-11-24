package school.sptech;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cglib.core.Local;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String nomeBucketTrusted = "trusted-1d4a3f130793f4b0dfc576791dd86b37";
        String nomeBucketClient = "client-1d4a3f130793f4b0dfc576791dd86b37";
        List<Captura> capturas = Gerenciador.leCsvBucketTrusted(nomeBucketTrusted);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");


        ObjectMapper mapper = new ObjectMapper();
        LocalDateTime agora = LocalDateTime.now();

        // Dashboard de Manutenção - João Vitor Lira
        Gerenciador.exibeListaCapturas(capturas);

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

        // Criação do objeto Json
        mapper.writeValue(new File(agora.format(formatador)+ "_manutencao" + ".json"), mapAuxiliar);

        File controladoresFile = new File(agora.format(formatador)+ "_manutencao" + ".json");
        try (S3Client s3 = S3Client.create()) {
            // Upload dos registros ultimos 6 registros dos controladores
            s3.putObject(PutObjectRequest.builder()
                    .bucket(nomeBucketClient)
                    .key("dashboard_manutencao/jsons/"+ agora.format(formatador)+ "_manutencao" +".json")
                    .build(), controladoresFile.toPath());
            System.out.println("Arquivos enviados com sucesso para o bucket " + nomeBucketClient);
        }

        //Dashboard de Robótica - João de Oliveira Neto

        Collections.reverse(capturas);
        Gerenciador.exibeListaCapturas(capturas);

        List<Captura> registrosControladores = DashBoardRoboticaNrt.ultimos6RegistosDeContraladores(capturas);
        Gerenciador.exibeListaCapturas(registrosControladores);

        //Criação do objeto Json
        mapper.writeValue(new File(agora.format(formatador) + "_NRT" + ".json"), registrosControladores);

        controladoresFile = new File(agora.format(formatador) + "_NRT" + ".json");
        try (S3Client s3 = S3Client.create()) {
            // Upload dos registros ultimos 6 registros dos controladores
            s3.putObject(PutObjectRequest.builder()
                    .bucket(nomeBucketClient)
                    .key("dashboardRoboticaNrt/jsons/"+ agora.format(formatador)+ "_NRT" +".json")
                    .build(), controladoresFile.toPath());
            System.out.println("Arquivos enviados com sucesso para o bucket " + nomeBucketClient);
        }


        // Dashboard de CPU e RAM - Erick Araújo Ferreira

        Gerenciador.exibeListaCapturas(capturas);
        Collections.reverse(capturas);
        DashCpuRam.exibirCapturasControlador(capturas, "0001");
        DashCpuRam.getMediaCpuRam(DashCpuRam.getDadosUltimaHora(capturas));
        Gerenciador.exibeListaCapturas(DashCpuRam.getDadosUltimaHora(capturas));
        DashCpuRam.getMediaCpuRamCada5Minutos(DashCpuRam.getDadosUltimaHora(capturas));
        DashCpuRam.getPicosRamCpu(DashCpuRam.getMediaCpuRamCada5Minutos(DashCpuRam.getDadosUltimaHora(capturas)));
        DashCpuRam.getUltimasCapturasCpuRam(capturas);

        Map<String, Map<String, Map<String, String>>> mapCpuRam = new HashMap<>();

        mapCpuRam.put("media5Minutos", DashCpuRam.getMediaCpuRamCada5Minutos(DashCpuRam.getDadosUltimaHora(capturas)));
        mapCpuRam.put("picoCpuRam", DashCpuRam.getPicosRamCpu(DashCpuRam.getMediaCpuRamCada5Minutos(DashCpuRam.getDadosUltimaHora(capturas))));
        mapCpuRam.put("ultimasCapturas", DashCpuRam.getUltimasCapturasCpuRam(capturas));

        //Criação do objeto Json
        mapper.writeValue(new File(agora.format(formatador)+ "_cpu_ram" + ".json"), mapCpuRam);

        controladoresFile = new File(agora.format(formatador)+ "_cpu_ram" + ".json");
        try (S3Client s3 = S3Client.create()) {
            // Upload dos registros ultimos 6 registros dos controladores
            s3.putObject(PutObjectRequest.builder()
                    .bucket(nomeBucketClient)
                    .key("dashboard_cpu_ram/jsons/"+ agora.format(formatador)+ "_cpu_ram" +".json")
                    .build(), controladoresFile.toPath());
            System.out.println("Arquivos enviados com sucesso para o bucket " + nomeBucketClient);
        }
    }
}
