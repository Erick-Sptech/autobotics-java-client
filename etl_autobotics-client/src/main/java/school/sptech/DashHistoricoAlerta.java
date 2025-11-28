package school.sptech;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashHistoricoAlerta {

//    public static void main(String[] args) throws IOException {
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
//
//        ObjectMapper mapper = new ObjectMapper();
//        LocalDateTime agora = LocalDateTime.now();
//
//        List<Captura> capturas = Gerenciador.leCsvBucketTrusted("trusted-1d4a3f130793f4b0dfc576791dd86b37");
//
//
//        mapper.writeValue(new File(agora.format(formatador) + ".json"), DashHistoricoAlerta.getMediasPorSetor(capturas));
//    }

    public static List<String> getSetores(List<Captura> capturas){

        List<String> setores = new ArrayList<>();

        for(Captura c : capturas){

            if(!setores.contains(c.getSetor())){
                System.out.println("Setor:" + c.getSetor());
                setores.add(c.getSetor());
            }
        }

        return setores;
    }

    public static Map<String,Map<String,String>> getMediasPorSetor(List<Captura> capturas){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Map<String,Map<String,String>> mapResultado = new HashMap<>();

        LocalDateTime horaAtual = LocalDateTime.now();

        List<String> setores = getSetores(capturas);

        for(String s : setores){
            Integer contador = 0;

            Double mediaCpu = 0.0;
            Double mediaRam = 0.0;
            Double mediaDisco = 0.0;

            Map<String,String> mapAox = new HashMap<>();

            for(Captura c : capturas){

                if(c.getSetor().equals(s) && LocalDateTime.parse(c.getTimestamp(), formatter).isAfter(horaAtual.minusMonths(1))){
                    System.out.println("Time stamp do objeto: " + c.getTimestamp());
                    mediaCpu += c.getCpu();
                    mediaRam += c.getRamUsada();
                    mediaDisco += c.getDiscoUsado();

                    contador++;
                }
            }

            mapAox.put("cpu", String.format("%.2f", (mediaCpu / contador)).replace(",", "."));
            mapAox.put("ram", String.format("%.2f", (mediaRam / contador)).replace(",", "."));
            mapAox.put("disco", String.format("%.2f",(mediaDisco / contador)).replace(",", "."));

            mapResultado.put(s, mapAox);

        }

        return mapResultado;
    }
}