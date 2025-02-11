package com.coltsoftware.jobb;

import java.io.File;
import java.io.IOException;

import static java.lang.System.out;

public class Main {

    public static void main(String[] stringArgs) throws IOException {

        Args args = new Args(stringArgs);
        boolean verbose = args.isVerbose();

        if (args.isHelp()) {
            printHelp();
            return;
        }

        String pathname = args.getDirectory();
        if (pathname == null) {
            out.println("A directory to be recursed through -d <directory> is required when creating an OBB filesystem.\n");
            printHelp();
            return;
        }

        File outputName = args.getObbFile();
        if (outputName == null) {
            out.println("An output filename -o <outputfile> is required when creating an OBB filesystem.");
            out.println("Or let the tool name it by specifying -pn <package> and -pv <version>.\n");
            printHelp();
            return;
        }

        Zipper zipper = new Zipper(new File(pathname), outputName);

        args.list("-0e").forEach(zipper::addNoCompressExtension);
        args.list("-0r").forEach(zipper::addNoCompressRegex);

        if (verbose) {
            out.println("Creating zip");
            if (zipper.getNoCompressPatterns().size() > 0) {
                out.println("These patterns will not be compressed");
                zipper.getNoCompressPatterns().forEach(out::println);
            }
        }

        Zipper.ZipResult zipResult = zipper.zip();

        if (verbose) {
            out.println("Files:");
            zipResult.getAddedFiles().forEach(out::println);
        }

        out.println("Complete");
    }

    private static void printHelp() {
        out.println("Good Jobb -- Create OBB files for use on Android\n" +
                "\n" +
                " -d <directory> Use <directory> as input for OBB files\n" +
                " -o <filename>  Write OBB file out to <filename>\n" +
                " -o <directory> Write OBB file out to <directory>\n" +
                " -v             Verbose mode\n" +
                " -h             Help; this usage screen\n" +
                " -pn <package>  Package name for OBB file\n" +
                " -pv <version>  Package version for OBB file\n" +
                " -patch         Is patch not main");
        out.println();
        out.println("0% compression options (repeatable)");
        out.println(" -0e <extension> Do not compress this extension. e.g. -0e .abc");
        out.println(" -0r <regex>     Do not compress files matching this regex. e.g. -0r .*\\\\file.abc");
    }
}
