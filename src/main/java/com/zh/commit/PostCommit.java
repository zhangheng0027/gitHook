package com.zh.commit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.File;
import java.util.Objects;

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

    private File generateLogFile(PostCommitOption p) {
        String logf = p.getLogFileName();
        if (Objects.isNull(logf) || "".equals(logf)) {
            System.out.println("未设置日志存放位置,  .get/hook/post-commit 文件的 logFileName 属性");
            System.exit(0);
        }
        File file = new File(logf);
        File pathFile = file.getParentFile();


        file.getName();
        return null;
    }

    private void displayLog(PostCommitOption p) {
        System.out.println("版本: " + p.getMd5());
        System.out.println("作者: " + p.getAuthor() + " <" + p.getMail() + ">");
        System.out.println("日期: " + p.getDate());
        System.out.println("信息:");
        System.out.println(p.getMsg());
        System.out.println();
        System.out.println("----");
    }

    private StringBuffer handleDiffFileName(String files) {
        StringBuffer sb = new StringBuffer(files.length());
        String[] fs = files.split(" ");


        return sb;
    }
}
