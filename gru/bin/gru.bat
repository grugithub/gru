@echo off
setlocal

set CLASSPATH=%GRU_HOME%\lib\ST4-4.0.7.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\antlr-2.7.7.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\antlr-java-parser-1.0.12.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\antlr-runtime-3.5.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\antlr4-4.0.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\antlr4-runtime-4.0.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\asm-4.0.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\asm-analysis-4.0.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\asm-commons-4.0.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\asm-tree-4.0.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\asm-util-4.0.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\commons-io-2.1.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\commons-io-2.1.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\groovy-2.1.5.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\gru-0.0.2.BUILD-SNAPSHOT.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\org.abego.treelayout.core-1.0.1.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\stringtemplate-3.2.1.jar;%CLASSPATH%
set CLASSPATH=%GRU_HOME%\lib\commons-lang3-3.1.jar;%CLASSPATH%

java com.github.gru.GruMain

endlocal
