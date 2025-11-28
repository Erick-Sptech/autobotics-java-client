package school.sptech;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ResultadoHistoricoDisco {
    private String codigo;
    private List<LocalDate> datas;
    private List<Double> medias;
    private List<Double> coeficientes;
    private List<Double> mudaCadaData;
    private Map<Integer, List<Double>> mediaSetor;
    private LocalDate dataCritica;
    private String quantoFalta;
    private Integer idSetor;

    public ResultadoHistoricoDisco(String codigo, List<LocalDate> datas, List<Double> medias, List<Double> coeficientes, List<Double> mudaCadaData, Map<Integer, List<Double>> mediaSetor, LocalDate dataCritica, String quantoFalta, Integer idSetor) {
        this.codigo = codigo;
        this.datas = datas;
        this.medias = medias;
        this.coeficientes = coeficientes;
        this.mudaCadaData = mudaCadaData;
        this.mediaSetor = mediaSetor;
        this.dataCritica = dataCritica;
        this.quantoFalta = quantoFalta;
        this.idSetor = idSetor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<LocalDate> getDatas() {
        return datas;
    }

    public void setDatas(List<LocalDate> datas) {
        this.datas = datas;
    }

    public List<Double> getMedias() {
        return medias;
    }

    public void setMedias(List<Double> medias) {
        this.medias = medias;
    }

    public List<Double> getCoeficientes() {
        return coeficientes;
    }

    public void setCoeficientes(List<Double> coeficientes) {
        this.coeficientes = coeficientes;
    }

    public List<Double> getMudaCadaData() {
        return mudaCadaData;
    }

    public void setMudaCadaData(List<Double> mudaCadaData) {
        this.mudaCadaData = mudaCadaData;
    }

    public Map<Integer, List<Double>> getMediaSetor() {
        return mediaSetor;
    }

    public void setMediaSetor(Map<Integer, List<Double>> mediaSetor) {
        this.mediaSetor = mediaSetor;
    }

    public LocalDate getDataCritica() {
        return dataCritica;
    }

    public void setDataCritica(LocalDate dataCritica) {
        this.dataCritica = dataCritica;
    }

    public String getQuantoFalta() {
        return quantoFalta;
    }

    public void setQuantoFalta(String quantoFalta) {
        this.quantoFalta = quantoFalta;
    }

    public Integer getIdSetor() {
        return idSetor;
    }

    public void setIdSetor(Integer idSetor) {
        this.idSetor = idSetor;
    }

    @Override
    public String toString() {
        return "ResultadoHistoricoDisco{" +
                "codigo='" + codigo + '\'' +
                ", datas=" + datas +
                ", medias=" + medias +
                ", coeficientes=" + coeficientes +
                ", mudaCadaData=" + mudaCadaData +
                ", mediaSetor=" + mediaSetor +
                ", dataCritica=" + dataCritica +
                ", quantoFalta='" + quantoFalta + '\'' +
                ", idSetor=" + idSetor +
                '}';
    }
}
