package com.lws.interview.questions.abstraction;

public interface LineParser<T> {
    T parseLine(String s);
}


class FixedLenLineParser implements LineParser<String> {

    @Override
    public String parseLine(String s) {
        //countyCode groupMember accountNumber openDate ....
        // fixed length string
        //SG HSBC 130663302380 20200519 ....
        return null;
    }
}


class CSVLineParser implements LineParser<String> {

    @Override
    public String parseLine(String s) {
        //countyCode groupMember accountNumber openDate ....
        // csv formant string
        //SG,HSBC,130663302380,20200519,....
        return null;
    }
}

