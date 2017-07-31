package utility;

import keyInterface.trialList;
import keyInterface.keyList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class exportUser {
    private String csvFile = "./data/";
    public boolean writeCSV(trialList userTrial, String id) {
        final String COMMA = ",";
        final String NEW_LINE = "\n";
        final String HEADER = "UserID,Trial No.,D_.,D_a,D_n,D_g,D_r,D_y,D_n,D_e,D_e,D_s,D_o,D_n,D_5,D_2,F_.-a,F_n,F_g,F_r,F_y,F_n,F_e,F_e,F_s,F_o,F_n,F_5,F_2";
        this.csvFile = this.csvFile + "all_data.csv";
        FileWriter fileWriter = null;
        File f = new File(csvFile);

        try {
            if (!f.exists()) {
                fileWriter = new FileWriter(this.csvFile, false);
                fileWriter.append(HEADER);
                fileWriter.append(NEW_LINE);
            } else {
                fileWriter = new FileWriter(this.csvFile, true);
            }
            for (int i = 0; i < userTrial.getSize(); i++) {
                fileWriter.append(id);
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(i + 1));
                fileWriter.append(COMMA);
                keyList n = userTrial.getElement(i);
                for (int j = 0; j < n.getSize(); j++) {
                    fileWriter.append(String.valueOf(n.getDwell(j)));
                    fileWriter.append(COMMA);

                }
                for (int j = 0; j < n.getSize() - 1; j++) {
                    fileWriter.append(String.valueOf(n.getFlight(j)));
                    fileWriter.append(COMMA);
                }
                fileWriter.append(NEW_LINE);
            }
        } catch (IOException e) {
            System.out.print(e);
            return false;
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                System.out.println("Error during flushing");
                e.printStackTrace();
            }
        }
        return true;
    }
}
