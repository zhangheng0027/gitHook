package com.zh.commit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * commit 后执行
 */
public class PostCommit {

    public PostCommit(String[] args) throws CmdLineException {
        final PostCommitOption pco = new PostCommitOption();
        final CmdLineParser parser = new CmdLineParser(pco);
        parser.parseArgument(args);

        System.out.println(pco.getBranch());
    }
}
