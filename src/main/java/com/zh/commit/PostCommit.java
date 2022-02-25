package com.zh.commit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * commit 后执行
 */
public class PostCommit {

    private PostCommit() {}

    public PostCommit(String[] args) throws Exception {
        final PostCommitOption pco = new PostCommitOption();
        final CmdLineParser parser = new CmdLineParser(pco);
        parser.parseArgument(args);


        File ignore = new File("ignoreBranch.txt");
        if (ignore.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(ignore));
            String[] brantchs = br.readLine().split(" ");
            for (String b : brantchs)
                if (pco.getBranch().equals(b)) {
                    exit("非开发分支，不进行日志写入",pco);
                }
        } else if ("master".equals(pco.getBranch()) || "test".equals(pco.getBranch())
                || "regression".equals(pco.getBranch()) || "production".equals(pco.getBranch())) {
            exit("非开发分支，不进行日志写入", pco);
        }

        try {
            File f = generateLogFile(pco);
            System.out.println(f.getPath());
            String con = displayLog(pco).toString();
//            FileWriter f = new FileWriter(f, true);
//            FileOutputStream fw = new FileOutputStream(f, true);
//
//
//            new OutputStreamWriter(fw, "UTF-8");
//            BufferedWriter bw = new BufferedWriter(fw);

            FileOutputStream fw = new FileOutputStream(f, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fw, "UTF-8"));

            bw.write(con);
            bw.close();
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exit("日志写入失败, 请手动操作", pco);
        }
    }

    public static void main(String[] args) throws IOException {
        PostCommitOption p = new PostCommitOption();
        p.setLogFileName("d:/信贷版本/crms-0225/list_20220225_13726_hzhang.txt");
        p.setBranch("dev-20220318");
        p.setMsg("dev-20220318 qc 13532 其他");
        new PostCommit().generateLogFile(p);
    }


    private File generateLogFile(PostCommitOption p) throws IOException {
        String logf = p.getLogFileName();
        if (Objects.isNull(logf) || "".equals(logf)) {
            System.out.println("未设置日志存放位置,  .get/hook/post-commit 文件的 logFileName 属性");
            exit(p);
        }
        File file = new File(logf);
        File pathFile = file.getParentFile();
        String pfn = pathFile.getName();
        // 解析文件夹
        String path = pathFile.getParent();
        Pattern pa = Pattern.compile("\\d+");
        Matcher m = pa.matcher(pfn);
        m.find();
        String ss = m.group();
        if (ss.length() == 8) {
            // 年月日
            // dev-20220225
            String b = p.getBranch().substring(4, 12);
            if (ss.equals(b)) {
                path = pathFile.getPath();
            } else {
                path = path + "/" + pfn.replaceAll(ss, b);
            }
        } else if(ss.length() == 4) {
            // 月日
            // dev-20220225
            String b = p.getBranch().substring(8, 12);
            if (ss.equals(b)) {
                path = pathFile.getPath();
            } else {
                path = path + "/" + pfn.replaceAll(ss, b);
            }
        }

//        list_20220225_13726_hzhang.txt
        path = path + "/list_" + p.getBranch().substring(4, 12) + "_";

        // dev-20220225 qc 13726....
        // 解析qc号
        Pattern p1 = Pattern.compile("(?<=[qQ][cC]) *\\d+");
        Matcher m1 = p1.matcher(p.getMsg());
        m1.find();
        path = path + m1.group().trim() + "_" + file.getName().split("_")[3];
        File nf = new File(path);
        if (nf.exists())
            return nf;

        if(!nf.getParentFile().exists()) {
            try {
                nf.getParentFile().mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        nf.createNewFile();
        return nf;
    }

    private void exit(PostCommitOption p) {
        exit("复制失败, 请主动将以下内容复制到相应文件", p);
    }

    private void exit(String ss, PostCommitOption p) {
        System.out.println(ss);
        System.out.println(displayLog(p));
        System.exit(0);
    }

    private StringBuffer displayLog(PostCommitOption p) {
        StringBuffer sb = new StringBuffer(256);
        sb.append("\r\n版本: " + p.getMd5());
        sb.append("\r\n作者: " + p.getAuthor() + " <" + p.getMail() + ">");
        sb.append("\r\n日期: " + p.getDate());
        sb.append("\r\n信息:\r\n");
        sb.append(p.getMsg());
        sb.append("\r\n----\r\n");
        sb.append(handleDiffFileName(p.getFileName()));
        sb.append("\r\n");
        return sb;
    }

    private StringBuffer handleDiffFileName(String files) {
        StringBuffer sb = new StringBuffer(files.length());
        String[] fst = files.split("\n");
        for (String f : fst) {
            String[] ts = f.split("\t");
            if ("A".equals(ts[0])) {
                sb.append("已添加: ");
            } else if ("D".equals(ts[0])) {
                sb.append("已删除: ");
            } else if ("M".equals(ts[0])) {
                sb.append("已修改: ");
            } else if ("R100".equals(ts[0])) {
                sb.append("重命名: ").append(ts[1])
                        .append(" (从 ").append(ts[2])
                        .append(")\r\n");
                continue;
            }
            sb.append(ts[1]).append("\r\n");
        }
        return sb;
    }
}
