package school.sptech;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static school.sptech.DashHistoricoDisco.executarEtlDisco;

public class Main {
    public static void rodarTratamento(){
        String nomeBucketTrusted = "trusted-1d4a3f130793f4b0dfc576791dd86b32";
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

        String nomeJsonHistoricoAlerta = "_historico_alerta";
        String pastaHistoricoAlerta = "dashboard_historico_alerta";

        executarEtlDisco(capturas);

        try{
            mapper.writeValue(new File("/tmp/" + agora.format(formatador)+ nomeJsonDashCpuRam + ".json"), DashCpuRam.criarJsonCpuRam(capturas));
            mapper.writeValue(new File("/tmp/" + agora.format(formatador)+ nomeJsonManutencao + ".json"), DashManutencao.criarJsonManutencao(capturas));
            mapper.writeValue(new File("/tmp/" + agora.format(formatador)+ nomeJsonRobotica + ".json"), DashBoardRoboticaNrt.ultimos6RegistosDeContraladores(capturas));
            mapper.writeValue(new File("/tmp/" + agora.format(formatador)+ nomeJsonHistoricoAlerta +".json"), DashHistoricoAlerta.getMediasPorSetor(capturas));
        }catch (IOException e){
            e.printStackTrace();
        }


        Gerenciador.enviaJsonParaBucketClient(agora, nomeJsonManutencao, pastaDashManutencao);
        Gerenciador.enviaJsonParaBucketClient(agora, nomeJsonDashCpuRam, pastaDashCpuRam);
        Gerenciador.enviaJsonParaBucketClient(agora, nomeJsonRobotica, pastaRoboticaNrt);
        Gerenciador.enviaJsonParaBucketClient(agora, nomeJsonHistoricoAlerta, pastaHistoricoAlerta);

    }
}