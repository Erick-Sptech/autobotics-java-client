package school.sptech;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cglib.core.Local;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String nomeBucketTrusted = "trusted-1d4a3f130793f4b0dfc576791dd86b37";
        List<Captura> capturas = Gerenciador.leCsvBucketTrusted(nomeBucketTrusted);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        ObjectMapper mapper = new ObjectMapper();
        LocalDateTime agora = LocalDateTime.now();

        String nomeJsonManutencao = "_manutencao";
        String pastaDashManutencao = "dashboard_manutencao";

        String nomeJsonRobotica = "_NRT";
        String pastaRoboticaNrt = "dashboard_robotica-nrt";

        String nomeJsonDashCpuRam = "_cpu_ram";
        String pastaDashCpuRam = "dashboard_cpu_ram";


        mapper.writeValue(new File(agora.format(formatador)+ nomeJsonManutencao + ".json"), DashManutencao.criarJsonManutencao(capturas));
        mapper.writeValue(new File(agora.format(formatador)+ nomeJsonDashCpuRam + ".json"), DashCpuRam.criarJsonCpuRam(capturas));
        mapper.writeValue(new File(agora.format(formatador)+ nomeJsonRobotica + ".json"), DashBoardRoboticaNrt.ultimos6RegistosDeContraladores(capturas));

        Gerenciador.enviaJsonParaBucketClient(agora, nomeJsonManutencao, pastaDashManutencao);
        Gerenciador.enviaJsonParaBucketClient(agora, nomeJsonDashCpuRam, pastaDashCpuRam);
        Gerenciador.enviaJsonParaBucketClient(agora, nomeJsonRobotica, pastaRoboticaNrt);
    }
}