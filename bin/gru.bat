@echo off

set GRU_CP=%GRU_HOME%\lib\ST4-4.0.7.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\antlr-2.7.7.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\antlr-java-parser-1.0.12.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\antlr-runtime-3.5.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\antlr4-4.0.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\antlr4-runtime-4.0.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\asm-4.0.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\asm-analysis-4.0.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\asm-commons-4.0.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\asm-tree-4.0.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\asm-util-4.0.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\commons-io-2.1.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\groovy-2.1.5.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\gru-0.0.1.BUILD-SNAPSHOT.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\org.abego.treelayout.core-1.0.1.jar;%GRU_CP%
set GRU_CP=%GRU_HOME%\lib\stringtemplate-3.2.1.jar;%GRU_CP%

java -cp %GRU_CP% com.github.gru.GruMain
:end