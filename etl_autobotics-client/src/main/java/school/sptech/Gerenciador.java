package school.sptech;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.nio.file.Paths;

public class Gerenciador {
    public Gerenciador() {

    }

    // RAW
    private static String buscarUltimoCsv(String nomeBucket) {
        // cria cliente s3 para requisitar a lista de objetos do bucket
        S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();

        // cria objetos request e response para buscar aws
        ListObjectsV2Request requisicao = ListObjectsV2Request.builder().bucket(nomeBucket).build();
        ListObjectsV2Response resultado = s3.listObjectsV2(requisicao);

        for (S3Object objetos : resultado.contents()) {
            System.out.println(objetos.key());
        }

        if (resultado.contents().isEmpty()) {
            System.out.println("Nenhum CSV foi encontrado...");
            return "empty";
        }

        // pega o ultimo CSV (ultimo item da lista resultado)
        return  resultado.contents().get(resultado.contents().size()-1).key();
    }

    private static String buscarUrlBucket(String nomeBucket, String nomeRegiao) {
        return String.format("https://%s.s3.%s.amazonaws.com", nomeBucket, nomeRegiao);
    }

    public static List<Captura> leCsvLocal(String nomeArq) {
        FileReader arq = null;
        Scanner entrada = null;
        Boolean erroGravar = false;
        // nomeArq += ".csv";
        if (!nomeArq.startsWith("/tmp/")) {
            nomeArq = "/tmp/" + nomeArq + ".csv";
        } else {
            nomeArq += ".csv";
        }
        List<Captura> listaCaptura = new ArrayList<>();
        Captura captura;

        try {
            System.out.println("Lendo CSV local...");
            arq = new FileReader(nomeArq);  // Abre o arquivo para leitura
            // Instancia a classe Scanner e especifica que deve usar delimitador ; ou \n
            entrada = new Scanner(arq).useDelimiter(";|\\n");
        }
        catch (FileNotFoundException erro) {
            System.out.println("Arquivo inexistente!");
            System.exit(1);
        }
        try {
            Boolean cabecalho = true;
            while (entrada.hasNextLine()) {
                // le a linha inteira do csv
                String linha = entrada.nextLine();
                System.out.println(linha);
                // divide essa linha em vários campos usando a virgula
                // campos: 0 - timestamp, 1 - cpu, 2 - ramTotal, 3 - ramUsada, 4 - discoTotal, 5 - discoUsado
                // 6 - numProcessos, 7 - codigoMaquina, 8 - top5Processos (sendo tratado como uma String inteira)

                String[] campos = linha.split(";", 11);

                if (cabecalho) {

                    for (int i = 0; i < campos.length; i++) {
                        campos[i] = campos[i].replaceAll("[^a-zA-Z0-9]", "");
                    }
                    //                    System.out.printf("%-25s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-20s %-20s %-10s\n",
                    //                            campos[0], campos[1], campos[2], campos[3], campos[4], campos[5],
                    //                            campos[6], campos[7], "empresa", "setor", campos[8]);
                    cabecalho = false;
                } else {
                    String timestamp = (!campos[0].isEmpty()) ? campos[0].replaceAll("\"", ""): "N/A";
                    Double cpu = (!campos[1].isEmpty()) ? Double.parseDouble(campos[1]) : null;
                    Double ramTotal = (!campos[2].isEmpty()) ? Double.parseDouble(campos[2]) : null;
                    Double ramUsada = (!campos[3].isEmpty()) ? Double.parseDouble(campos[3]) : null;
                    Double discoTotal = (!campos[4].isEmpty()) ? Double.parseDouble(campos[4]) : null;
                    Double discoUsado = (!campos[5].isEmpty()) ? Double.parseDouble(campos[5]) : null;
                    Integer numProcessos = (!campos[6].isEmpty()) ? Integer.parseInt(campos[6]) : null;
                    String codigoMaquina = (!campos[7].isEmpty()) ? campos[7].replaceAll("\"", "") : "N/A";
                    String nomeEmpresa = (!campos[8].isEmpty()) ? campos[8].replaceAll("\"", "") : "N/A";;
                    String nomeSetor = (!campos[9].isEmpty()) ? campos[9].replaceAll("\"", "") : "N/A";;
                    String top5Processos = (!campos[10].isEmpty()) ? campos[10] : "N/A";


                    captura = new Captura(timestamp, cpu, ramTotal, ramUsada, discoTotal, discoUsado, numProcessos, codigoMaquina, nomeEmpresa, nomeSetor, top5Processos);
                    listaCaptura.add(captura);

                    //                    System.out.printf(
                    //                            "%-25s %-10.2f %-10.2f %-10.2f %-10.2f %-10.2f %-15d %-15s %-20s %-20s %-10s\n",
                    //                            timestamp, cpu, ramTotal, ramUsada, discoTotal, discoUsado, numProcessos,
                    //                            codigoMaquina, nomeEmpresa, nomeSetor, top5Processos
                    //                    );
                }
            }
        }
        catch (NoSuchElementException erro) {
            System.out.println("Arquivo com problemas!");
            erro.printStackTrace();
            erroGravar = true;
        }
        catch (IllegalStateException erro) {
            System.out.println("Erro na leitura do arquivo!");
            erro.printStackTrace();
            erroGravar = true;
        }

        return listaCaptura;
    }

    public static void exibeListaCapturas(List<Captura> lista) {
        System.out.printf("%-25s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-20s %-20s %-10s\n",
                "timestamp", "cpu", "ramTotal", "ramUsada", "discoTotal", "discoUsado",
                "numProcessos", "codigoMaquina", "empresa", "setor", "top5Processos");
        for (Captura c : lista) {
            System.out.printf(
                    "%-25s %-10.2f %-10.2f %-10.2f %-10.2f %-10.2f %-15d %-15s %-20s %-20s %-10s\n",
                    c.getTimestamp(), c.getCpu(), c.getRamTotal(), c.getRamUsada(), c.getDiscoTotal(), c.getDiscoUsado(), c.getNumProcessos(),
                    c.getCodigoMaquina(), c.getEmpresa(), c.getSetor(), c.getTop5Processos()
            );
        }
    }

    // TRUSTED

    public static List<Captura> leCsvBucketTrusted(String nomeBucket) {
        String nomeArq = buscarUltimoCsv(nomeBucket);

        // caso não haja nenhum arquivo no trusted ainda
        if (nomeArq.equals("empty")) {
            return new ArrayList<>();
        }

        String urlBucket = buscarUrlBucket(nomeBucket, "us-east-1");
        List<Captura> listaCaptura = new ArrayList<>();
        Captura captura;

        InputStream arq = null;
        Scanner entrada = null;
        Boolean erroGravar = false;

        try {
            System.out.println("Link do csv: " + urlBucket+"/"+nomeArq);
            // cria um objeto URL do CSV no Bucket
            URL url = new URL(urlBucket+"/"+nomeArq);
            // openStream faz requisicao HTTPS e retorna um InputStream
            arq = url.openStream();

            entrada = new Scanner(arq).useDelimiter(",|\\n");
        }
        catch (IOException erro) {
            System.out.println("Erro na URL Bucket");
            erro.printStackTrace();
            System.exit(1);
        }

        try {
            Boolean cabecalho = true;
            while (entrada.hasNextLine()) {
                // le a linha inteira do csv
                String linha = entrada.nextLine();
                // divide essa linha em vários campos usando a virgula
                // campos: 0 - timestamp, 1 - cpu, 2 - ramTotal, 3 - ramUsada, 4 - discoTotal, 5 - discoUsado
                // 6 - numProcessos, 7 - codigoMaquina, 8 - top5Processos (sendo tratado como uma String inteira)

                String[] campos = linha.split(";", 11);

                if (cabecalho) {

                    for (int i = 0; i < campos.length; i++) {
                        campos[i] = campos[i].replaceAll("[^a-zA-Z0-9]", "");
                    }
//                    System.out.printf("%-25s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-20s %-20s %-10s\n",
//                            campos[0], campos[1], campos[2], campos[3], campos[4], campos[5],
//                            campos[6], campos[7], "empresa", "setor", campos[8]);
                    cabecalho = false;
                }
                else {
                    String timestamp = (!campos[0].isEmpty()) ? campos[0].replaceAll("\"", "") : "N/A";
                    Double cpu = (!campos[1].isEmpty()) ? Double.parseDouble(campos[1]) : null;
                    Double ramTotal = (!campos[2].isEmpty()) ? Double.parseDouble(campos[2]) : null;
                    Double ramUsada = (!campos[3].isEmpty()) ? Double.parseDouble(campos[3]) : null;
                    Double discoTotal = (!campos[4].isEmpty()) ? Double.parseDouble(campos[4]) : null;
                    Double discoUsado = (!campos[5].isEmpty()) ? Double.parseDouble(campos[5]) : null;
                    Integer numProcessos = (!campos[6].isEmpty()) ? Integer.parseInt(campos[6]) : null;
                    String codigoMaquina = (!campos[7].isEmpty()) ? campos[7].replaceAll("\"", "") : "N/A";
                    String nomeEmpresa = (!campos[8].isEmpty()) ? campos[8].replaceAll("\"", "") : "N/A";
                    ;
                    String nomeSetor = (!campos[9].isEmpty()) ? campos[9].replaceAll("\"", "") : "N/A";
                    ;
                    String top5Processos = (!campos[10].isEmpty()) ? campos[10] : "N/A";

//
                    captura = new Captura(timestamp, cpu, ramTotal, ramUsada, discoTotal, discoUsado, numProcessos, codigoMaquina, nomeEmpresa, nomeSetor, top5Processos);
                    listaCaptura.add(captura);

//                    System.out.printf(
//                            "%-25s %-10.2f %-10.2f %-10.2f %-10.2f %-10.2f %-15d %-15s %-20s %-20s %-10s\n",
//                            timestamp, cpu, ramTotal, ramUsada, discoTotal, discoUsado, numProcessos,
//                            codigoMaquina, nomeEmpresa, nomeSetor, top5Processos
//                    );
                }
            }
        }
        catch (NoSuchElementException erro) {
            System.out.println("Arquivo com problemas!");
            erro.printStackTrace();
            erroGravar = true;
        }
        catch (IllegalStateException erro) {
            System.out.println("Erro na leitura do arquivo!");
            erro.printStackTrace();
            erroGravar = true;
        }
        finally {
            try {
                entrada.close();
                arq.close();
            }
            catch (IOException erro) {
                System.out.println("Erro ao fechar o arquivo");
                erroGravar = true;
            }
            if (erroGravar) {
                System.exit(1);
            }
        }
        return listaCaptura;
    }

    public static void enviaJsonParaBucketClient(LocalDateTime agora, String nomeDashboard, String nomePasta){
        String nomeBucketClient = "client-1d4a3f130793f4b0dfc576791dd86b32";
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        String nomeArquivo = agora.format(formatador) + nomeDashboard + ".json";
        File controladoresFile = new File("/tmp/" + nomeArquivo);
        try (S3Client s3 = S3Client.create()) {
            // Upload dos registros ultimos 6 registros dos controladores
            s3.putObject(PutObjectRequest.builder()
                    .bucket(nomeBucketClient)
                    .key(nomePasta + "/jsons/"+ nomeArquivo)
                    .build(), controladoresFile.toPath());
            System.out.println("Arquivos enviados com sucesso para o bucket " + nomeBucketClient);
        }
    }
}