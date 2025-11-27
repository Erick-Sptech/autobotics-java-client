package school.sptech;
import software.amazon.awssdk.services.s3.S3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashBoardRoboticaNrt {

    public static Map<String, List<Captura>> ultimos6RegistosDeContraladores(List<Captura> lista) {
        Map<String, List<Captura>> mapAux = new TreeMap<>();

        List<String> controladoresRecentes = new ArrayList<>();
        for (int i = lista.size() - 1; i >= 0 && controladoresRecentes.size() < lista.size(); i--) {
            String codigo = lista.get(i).getCodigoMaquina();
            if (!controladoresRecentes.contains(codigo)) {
                controladoresRecentes.add(codigo);
            }
        }
        for (String controlador : controladoresRecentes) {
            List<Captura> listaAux = new ArrayList<>();
            int count = 0;
            for (int i = lista.size() - 1; i >= 0 && count < 6; i--) {
                if (lista.get(i).getCodigoMaquina().equals(controlador)) {
                    listaAux.add(lista.get(i));
                    count++;
                }
            }
            mapAux.put(controlador, listaAux);
        }
        return mapAux;
    }
}