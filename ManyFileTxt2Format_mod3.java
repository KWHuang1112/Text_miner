import java.io.*;
import java.util.ArrayList;

public class ManyFileTxt2Format_mod3 {
    public ManyFileTxt2Format_mod3(String dir) throws Exception {
        File rootDir = new File(dir);
        String[] subDirs = rootDir.list();

        if (subDirs == null) return;

        for (String subDirName : subDirs) {
            File subDir = new File(rootDir, subDirName);

            if (subDir.isDirectory()) {
                String[] files = subDir.list();

                if (files == null) continue;

                for (String fileName : files) {
                    if (fileName.endsWith(".txt")) {
                        String filePath = subDir.getAbsolutePath() + File.separator + fileName;
                        String pubmedTag = subDirName + "\t" + fileName.replaceAll(" ", "_");

                        System.out.println("Processing: " + pubmedTag);

//                        new Txt2Format(filePath, pubmedTag);
//                        new Txt2Hz(filePath, pubmedTag);
                        new Txt2ShortHz(filePath, pubmedTag);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java ManyFileTxt2Format_mod3 <directory>");
            return;
        }
        new ManyFileTxt2Format_mod3(args[0]);
    }
}




class Txt2Format {
    public Txt2Format(String filename, String pubmedID) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(" ");
        }
        br.close();
        String[] sentences = sb.toString().split("(?<=[.!?])\\s+");

        for (String sentence : sentences) {
            if (sentence.toUpperCase().contains("HZ")) {
                System.out.println(pubmedID + "\t" + sentence.trim());
            }
        }
    }
}



class Txt2Hz {

    public Txt2Hz(String filename, String pubmedID) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(" ");
        }
        br.close();

        String[] sentences = sb.toString().split("(?<=[.!?])\\s+");

        for (String sentence : sentences) {
            String upper = sentence.toUpperCase().trim();

            if (upper.contains("HZ")) {
                ArrayList<Integer> hzPosList = getHzPositions(upper);

                for (int pos : hzPosList) {
                    int start = Math.max(0, pos - 20);
                    int end = Math.min(upper.length(), pos + 2);
                    String context = upper.substring(start, end);
                    System.out.println(pubmedID + "\t" + sentence.trim() + "\t" + context);
                }
            }
        }
    }

    private ArrayList<Integer> getHzPositions(String str) {
        ArrayList<Integer> positions = new ArrayList<>();
        int index = str.indexOf("HZ");
        while (index != -1) {
            positions.add(index + 2);
            index = str.indexOf("HZ", index + 2);
        }
        return positions;
    }
}



class Txt2ShortHz {

    public Txt2ShortHz(String filename, String pubmedID) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(" ");
        }
        br.close();

        String[] sentences = sb.toString().split("(?<=[.!?])\\s+");

        for (String sentence : sentences) {
            String upper = sentence.toUpperCase().trim();

            if (upper.contains("HZ")) {
                ArrayList<Integer> hzPosList = getHzPositions(upper);

                for (int pos : hzPosList) {
                    int start = Math.max(0, pos - 20);
                    int end = Math.min(upper.length(), pos + 2);
                    String snippet = upper.substring(start, end);
                    String digits = extractNumbers(snippet);
                    System.out.println(pubmedID + "\t" + sentence.trim() + "\t" + snippet + "\t" + digits);
                }
            }
        }
    }

    private ArrayList<Integer> getHzPositions(String str) {
        ArrayList<Integer> positions = new ArrayList<>();
        int index = str.indexOf("HZ");
        while (index != -1) {
            positions.add(index + 2);
            index = str.indexOf("HZ", index + 2);
        }
        return positions;
    }

    private String extractNumbers(String snippet) {
        snippet = snippet.replaceAll("AND", " AND ")
                         .replaceAll("TO", " TO ")
                         .replaceAll("[,()\\–∼-]", " ");
        String[] tokens = snippet.split(" ");
        StringBuilder result = new StringBuilder();
        for (String token : tokens) {
            try {
                double value = Double.parseDouble(token.trim());
                result.append(value).append(", ");
            } catch (NumberFormatException ignored) {}
        }
        return result.toString();
    }
}



