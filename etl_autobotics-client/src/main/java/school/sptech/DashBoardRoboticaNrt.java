package school.sptech;
import software.amazon.awssdk.services.s3.S3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashBoardRoboticaNrt {
    public static void main(String[] args) throws Exception{
        String nomeBucketTrusted = "trusted-1d4a3f130793f4b0dfc576791dd86b37";
        List<Captura> lista = Gerenciador.leCsvBucketTrusted(nomeBucketTrusted);
        Collections.reverse(lista);
        Gerenciador.exibeListaCapturas(lista);

        List<Captura> registrosControladores = ultimos6RegistosDeContraladores(lista);
        Gerenciador.exibeListaCapturas(registrosControladores);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(LocalDateTime.now().format(formatador) + ".json"), registrosControladores);
        String bucketName = "client-1d4a3f130793f4b0dfc576791dd86b37";

        File controladoresFile = new File(LocalDateTime.now().format(formatador) + ".json");
        try (S3Client s3 = S3Client.create()) {
            // Upload dos registros ultimos 6 registros dos controladores
            s3.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key("/dashboardRoboticaNrt/jsons/"+ LocalDateTime.now().format(formatador) +".json")
                    .build(), controladoresFile.toPath());
            System.out.println("Arquivos enviados com sucesso para o bucket " + bucketName);
        }
    }

    public static List<Captura> ultimos6RegistosDeContraladores(List<Captura> lista) {
        List<Captura> matriz = new ArrayList<>();
        List<String> controladoresRecentes = new ArrayList<>();
        if (lista == null || lista.isEmpty()) {
            return matriz;
        }
        for (int i = lista.size() - 1; i >= 0 && controladoresRecentes.size() < lista.size(); i--) {
            String codigo = lista.get(i).getCodigoMaquina();
            if (!controladoresRecentes.contains(codigo)) {
                controladoresRecentes.add(codigo);
            }
        }
        for (String controlador : controladoresRecentes) {
            int count = 0;
            for (int i = lista.size() - 1; i >= 0 && count < 6; i--) {
                if (lista.get(i).getCodigoMaquina().equals(controlador)) {
                    matriz.add(lista.get(i));
                    count++;
                }
            }
        }
        return matriz;
    }
}