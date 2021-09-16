package sample;
import java.text.DecimalFormat;

public class TestFile {
    private String fileName;
    private double spamProbability;
    private String actualClass;
    private String spamProbRounded;

    public TestFile(String fileName, double spamProbability, String actualClass){
        this.fileName = fileName;
        this.spamProbability = spamProbability;
        this.actualClass = actualClass;
    }

    public double getSpamProbability(){
        return this.spamProbability;
    }

    public String getSpamProbRounded(){
        DecimalFormat df = new DecimalFormat("0.00000");
        return df.format(this.spamProbability);
    }

    public String getActualClass(){
        return this.actualClass;
    }

    public void setFilename(String value){
        this.fileName = value;
    }

    public String getFileName(){
        return this.fileName;
    }

    public void setSpamProbability(double val){
        this.spamProbability = val;
    }

    public void setActualClass(String value){
        this.actualClass=value;
    }
}