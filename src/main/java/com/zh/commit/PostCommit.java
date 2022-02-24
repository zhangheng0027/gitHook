package com.zh.commit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
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


        if ("master".equals(pco.getBranch()) || "test".equals(pco.getBranch())
            || "regression".equals(pco.getBranch()) || "production".equals(pco.getBranch())) {
            exit("非开发分支，不进行日志写入", pco);
        }

        File ignore = new File("ignoreBranch.txt");
        if (ignore.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(ignore));
            String[] brantchs = br.readLine().split(" ");
            for (String b : brantchs)
                if (pco.getBranch().equals(b)) {
                    exit("非开发分支，不进行日志写入",pco);
                }

        }
        File f = generateLogFile(pco);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        bw.write(displayLog(pco).toString());
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
        String path = pathFile.getPath();
        Pattern pa = Pattern.compile("\\d+");
        Matcher m = pa.matcher(pfn);
        m.find();
        String ss = m.group();
        if (ss.length() == 8) {
            // 年月日
            // dev-20220225
            String b = p.getBranch().substring(4, 12);
            if (ss.equals(b)) {
                path = pathFile.getParent();
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
        sb.append("\n版本: " + p.getMd5());
        sb.append("\n作者: " + p.getAuthor() + " <" + p.getMail() + ">");
        sb.append("\n日期: " + p.getDate());
        sb.append("\n信息:");
        sb.append(p.getMsg());
        sb.append("\n----\n");
        sb.append(handleDiffFileName(p.getFileName()));
        return sb;
    }

    private StringBuffer handleDiffFileName(String files) {
        StringBuffer sb = new StringBuffer(files.length());
        String[] fs = files.split(" ");
        for (int i = 0; i < fs.length - 1; i+=2) {
            if ("A".equals(fs[i])) {
                sb.append("已添加: ");
            } else if ("D".equals(fs[i])) {
                sb.append("已删除: ");
            } else if ("M".equals(fs[i])) {
                sb.append("已修改: ");
            } else if ("R100".equals(fs[i])) {
                sb.append("重命名: ").append(fs[i + 1])
                        .append(" (从 ").append(fs[i + 2])
                        .append(")\n");
                continue;
            }
            sb.append(fs[i+1]).append("\n");
        }

        return sb;
    }
}
