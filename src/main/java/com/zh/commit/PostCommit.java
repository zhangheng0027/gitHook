package com.zh.commit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * commit 后执行
 */
public class PostCommit {

    private PostCommit() {}

    public PostCommit(String[] args) throws CmdLineException {
        final PostCommitOption pco = new PostCommitOption();
        final CmdLineParser parser = new CmdLineParser(pco);
        parser.parseArgument(args);

        if ("master".equals(pco.getBranch()) || "test".equals(pco.getBranch())
            || "regression".equals(pco.getBranch()) || "production".equals(pco.getBranch())) {
            System.out.println("非开发分支, 不进行日志写入");
            return;
        }


    }

    public static void main(String[] args) throws CmdLineException, IOException {
        String s = "list_20220225_13726_hzhang.txt";

        PostCommitOption p = new PostCommitOption();
        p.setLogFileName("d:/crms-0225/list_20220225_13726_hzhang.txt");
        p.setBranch("dev-20220225");
        p.setMsg("dev-20220225 qc 15001 其他事项");
        System.out.println(s.split("_")[3]);
        File f = new PostCommit().generateLogFile(p);
        System.out.println(f.getPath());
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
        System.out.println("复制失败, 请主动将以下内容复制到相应文件");
        displayLog(p);
        System.exit(0);
    }

    private void displayLog(PostCommitOption p) {
        System.out.println("版本: " + p.getMd5());
        System.out.println("作者: " + p.getAuthor() + " <" + p.getMail() + ">");
        System.out.println("日期: " + p.getDate());
        System.out.println("信息:");
        System.out.println(p.getMsg());
        System.out.println();
        System.out.println("----");
        System.out.println(handleDiffFileName(p.getFileName()));
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
