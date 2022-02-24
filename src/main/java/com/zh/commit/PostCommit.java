package com.zh.commit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * commit 后执行
 */
public class PostCommit {

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

    public static void main(String[] args) {
        String s = "crms-0312";
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);
        m.find();
        String ss = m.group();
        System.out.println(ss);

    }



    private File generateLogFile(PostCommitOption p) {
        String logf = p.getLogFileName();
        if (Objects.isNull(logf) || "".equals(logf)) {
            System.out.println("未设置日志存放位置,  .get/hook/post-commit 文件的 logFileName 属性");
            exit(p);
        }
        File file = new File(logf);
        File pathFile = file.getParentFile();
        String branch = p.getBranch();
        String pfn = pathFile.getName();
        Pattern pa = Pattern.compile("\\d+");
        Matcher m = pa.matcher(pfn);
        m.find();
        String ss = m.group();
        if (ss.length() == 8) {

        } else if(ss.length() == 4) {

        }
        file.getName();
        return null;
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
