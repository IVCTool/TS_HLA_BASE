FROM alpine:3.3
COPY ./build/distributions/TS_HLA_EncodingRulesTester-1.0.1 /root/conf/TestSuites/TS_HLA_EncodingRulesTester
VOLUME ["/root/conf/TestSuites/TS_HLA_EncodingRulesTester"]
ENTRYPOINT [ "sh", "-c", "if [ -n \"$1\" ]; then cp -r /root/conf/TestSuites/* $1; echo \"Copied data to $1\"; else /bin/true; fi", "--" ]