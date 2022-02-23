package com.zh;


import com.zh.commit.PostCommit;
import org.kohsuke.args4j.CmdLineException;

import java.util.Arrays;

public class Main {


    public static void main(String[] args) throws CmdLineException {
        if (args.length < 1)
            return;
        System.out.println(args.length);

        if ("--PC".equalsIgnoreCase(args[0])) {
            String[] strs = new String[args.length - 1];
            System.arraycopy(args, 1, strs, 0,
                    args.length - 1);
            PostCommit pc = new PostCommit(strs);
        }


    }



}
