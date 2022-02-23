package com.zh;


import com.zh.commit.PostCommit;

import java.util.Arrays;

public class Main {


    public static void main(String[] args) {
        if (args.length < 1)
            return;

        if ("--PC".equalsIgnoreCase(args[0])) {
            String[] strs = new String[args.length - 1];
            System.arraycopy(args, 1, strs, 0,
                    args.length - 1);
            PostCommit pc = new PostCommit(strs);
        }


    }



}
