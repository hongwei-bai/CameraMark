package jd.aibdp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Created by cdy on 2016/2/3.
 * Introduced by zhoumin117
 * Modified by baihongwei1
 */
public class DimenTool {

    private static boolean GENERATE_FROM_XXHDPI = true;

    private static String APP_NAME = "app";

    public static void main(String[] args) {
        gen();
    }

    private static boolean isPx(String line) {
        return line.contains("px</dimen>");
    }

    private static boolean isDimenTest(String line) {
        return line.contains("dimen_test");
    }

    private static boolean isHorizontalDimension(String line) {
        return line.contains("_x\"")
                || line.contains("_margin_left") || line.contains("_margin_right")
                || line.contains("_margin_start") || line.contains("_margin_end");
    }

    public static void gen() {
        gen("");
    }

    public static void gen(String suffix) {
        gen("res", suffix);
    }

    public static void gen(String resDir, String suffix) {
        File file = null;
        if (GENERATE_FROM_XXHDPI) {
            file = new File("./" + APP_NAME + "/src/main/" + resDir + "/values-xxhdpi/dimens" + suffix + ".xml");
        } else {
            file = new File("./" + APP_NAME + "/src/main/" + resDir + "/values/dimens" + suffix + ".xml");
        }

        BufferedReader reader = null;
        StringBuilder mdpi = new StringBuilder();
        StringBuilder hdpi_213dpi = new StringBuilder();
        StringBuilder hdpi = new StringBuilder();
        StringBuilder xhdpi = new StringBuilder();
        StringBuilder m3pad = new StringBuilder();
        StringBuilder _default = new StringBuilder();
        StringBuilder xxhdpi = new StringBuilder();
        StringBuilder xxxhdpi_1440x2560 = new StringBuilder();
        StringBuilder xxxhdpi = new StringBuilder();

        int resourceLabelLineNo = -1;
        int lineNo = 0;

        try {
            reader = new BufferedReader(new FileReader(file));

            String tempString;

            int line = 1;

            while ((tempString = reader.readLine()) != null) {

                lineNo++;
                if (-1 == resourceLabelLineNo && tempString.contains("<resources>")) {
                    resourceLabelLineNo = lineNo;
                }

                if (lineNo == resourceLabelLineNo + 1 && suffix.isEmpty()) {
                    _default.append("    <dimen name=\"dimen_test\">110px</dimen>\r\n");
                    mdpi.append("    <dimen name=\"dimen_test\">111px</dimen>\r\n");
                    hdpi_213dpi.append("    <dimen name=\"dimen_test\">213px</dimen>\r\n");
                    hdpi.append("    <dimen name=\"dimen_test\">112px</dimen>\r\n");
                    xhdpi.append("    <dimen name=\"dimen_test\">113px</dimen>\r\n");
                    m3pad.append("    <dimen name=\"dimen_test\">114px</dimen>\r\n");
                    xxhdpi.append("    <dimen name=\"dimen_test\">115px</dimen>\r\n");
                    xxxhdpi_1440x2560.append("    <dimen name=\"dimen_test\">116px</dimen>\r\n");
                    xxxhdpi.append("    <dimen name=\"dimen_test\">117px</dimen>\r\n");
                }

                if (isDimenTest(tempString)) {

                } else if (tempString.contains("</dimen>") && !isPx(tempString)) {

                    //tempString = tempString.replaceAll(" ", "");

                    String start = tempString.substring(0, tempString.indexOf(">") + 1);

                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);

                    Double num = Double.parseDouble
                            (tempString.substring(tempString.indexOf(">") + 1,
                                    tempString.indexOf("</dimen>") - 2));

                    Double base = 0.0;
                    if (GENERATE_FROM_XXHDPI) {
                        base = num * 3;
                    } else {
                        base = num;
                    }

                    _default.append(start).append(keep2(base)).append(end).append("\r\n");

                    mdpi.append(start).append(keep2(base / 1.0)).append(end).append("\r\n");

                    hdpi_213dpi.append(start).append(keep2(base / 1.333)).append(end).append("\r\n");

                    hdpi.append(start).append(keep2(base / 1.5)).append(end).append("\r\n");

                    xhdpi.append(start).append(keep2(base / 2)).append(end).append("\r\n");

                    m3pad.append(start).append(keep2(base / 2.25)).append(end).append("\r\n");

                    xxhdpi.append(start).append(keep2(base / 3)).append(end).append("\r\n");

                    xxxhdpi_1440x2560.append(start).append(keep2(base / 2.63)).append(end).append("\r\n");

                    xxxhdpi.append(start).append(keep2(base / 4)).append(end).append("\r\n");

                } else {
                    // keep original format for non-dimen line. e.g. comments, gap
                    _default.append(tempString).append("\r\n");

                    mdpi.append(tempString).append("\r\n");

                    hdpi_213dpi.append(tempString).append("\r\n");

                    hdpi.append(tempString).append("\r\n");

                    xhdpi.append(tempString).append("\r\n");

                    m3pad.append(tempString).append("\r\n");

                    xxhdpi.append(tempString).append("\r\n");

                    xxxhdpi_1440x2560.append(tempString).append("\r\n");

                    xxxhdpi.append(tempString).append("\r\n");

                }

                line++;

            }

            reader.close();
//            System.out.println("<!--  hdpi -->");
//
//            System.out.println(hdpi);
//
//            System.out.println("<!--  xhdpi -->");
//
//            System.out.println(xhdpi);
//
//            System.out.println("<!--  xxhdpi -->");
//
//            System.out.println(xxhdpi);
//
//            System.out.println("<!--  xxxhdpi -->");
//
//            System.out.println(xxxhdpi);

            //文件夹
            String f_default_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values/";

            String f_mdpi_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values-mdpi/";

            String f_hdpi_213dpi_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values-213dpi/";

            String f_hdpi_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values-hdpi/";

            String f_xhdpi_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values-xhdpi/";

            String f_m3pad_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values-xxhdpi-1600x2560/";

            String f_xxhdpi_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values-xxhdpi/";

            String f_xxxhdpi1440x2560_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values-xxxhdpi-1440x2560/";

            String f_xxxhdpi_dir = "./" + APP_NAME + "/src/main/" + resDir + "/values-xxxhdpi/";

            //dimen文件
            String f_default = f_default_dir + "dimens" + suffix + ".xml";
            String f_mdpi = f_mdpi_dir + "dimens" + suffix + ".xml";
            String f_hdpi_213dpi = f_hdpi_213dpi_dir + "dimens" + suffix + ".xml";
            String f_hdpi = f_hdpi_dir + "dimens" + suffix + ".xml";
            String f_xhdpi = f_xhdpi_dir + "dimens" + suffix + ".xml";
            String f_m3pad = f_m3pad_dir + "dimens" + suffix + ".xml";
            String f_xxhdpi = f_xxhdpi_dir + "dimens" + suffix + ".xml";
            String f_xxxhdpi1440x2560 = f_xxxhdpi1440x2560_dir + "dimens" + suffix + ".xml";
            String f_xxxhdpi = f_xxxhdpi_dir + "dimens" + suffix + ".xml";

            //创建目录
            if (!new File(f_default_dir).exists()) new File(f_default_dir).mkdirs();
            if (!new File(f_mdpi_dir).exists()) new File(f_mdpi_dir).mkdirs();
            if (!new File(f_hdpi_213dpi_dir).exists()) new File(f_hdpi_213dpi_dir).mkdirs();
            if (!new File(f_hdpi_dir).exists()) new File(f_hdpi_dir).mkdirs();
            if (!new File(f_xhdpi_dir).exists()) new File(f_xhdpi_dir).mkdirs();
            if (!new File(f_m3pad_dir).exists()) new File(f_m3pad_dir).mkdirs();
            if (!new File(f_xxhdpi_dir).exists()) new File(f_xxhdpi_dir).mkdirs();
            if (!new File(f_xxxhdpi1440x2560_dir).exists()) new File(f_xxxhdpi1440x2560_dir).mkdirs();
            if (!new File(f_xxxhdpi_dir).exists()) new File(f_xxxhdpi_dir).mkdirs();

            writeFile(f_mdpi, mdpi.toString());

            writeFile(f_hdpi_213dpi, hdpi_213dpi.toString());

            writeFile(f_hdpi, hdpi.toString());

            writeFile(f_xhdpi, xhdpi.toString());

            writeFile(f_m3pad, m3pad.toString());

            if (GENERATE_FROM_XXHDPI) {
                writeFile(f_default, _default.toString());
            } else {
                writeFile(f_xxhdpi, xxhdpi.toString());
            }

            writeFile(f_xxxhdpi1440x2560, xxxhdpi_1440x2560.toString());

            writeFile(f_xxxhdpi, xxxhdpi.toString());

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (reader != null) {

                try {

                    reader.close();

                } catch (IOException e1) {

                    e1.printStackTrace();

                }

            }

        }

    }

    public static void writeFile(String file, String text) {

        PrintWriter out = null;

        try {

            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            out.println(text);

        } catch (IOException e) {

            e.printStackTrace();

        }


        out.close();

    }

    public static double keep2(double f) {
        BigDecimal bg = new BigDecimal(f);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }
}
