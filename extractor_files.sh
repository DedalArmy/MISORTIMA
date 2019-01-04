#!/bin/bash
function CreateReportsProjects
{
    local repoDir=$1
    local workingDir=$(pwd $repoDir)
    touch projects_list.txt
    for repository in $repoDir/*
    do
        for project in $repository/*
        do
            if [ -d $project ]
            then
              local repoDir=$(basename $repository)
              local projectDir=$(basename $project)
              local report_path_additional=$workingDir/$repoDir/$projectDir/report_imt_beans.txt
              local report_path_cloc=$workingDir/$repoDir/$projectDir/report_imt_cloc.txt

              echo "--> Current project : $repoDir/$projectDir <--"

              echo "Generation report_imt_cloc.txt"
              cloc $workingDir/$repoDir/$projectDir --exclude-dir=.cache,test,.git --quiet --csv > $report_path_cloc || exit

              #Count the number of XML file which contain
              local nb_xml_bean_config=$(find $workingDir/$repoDir/$projectDir -name "*.xml" -type f -not -path "*/test/*" | xargs grep -lw '<beans' | xargs grep -lw '<bean' | wc -l)
              local nb_java_bean_config=$(find $workingDir/$repoDir/$projectDir -name '*.java' -type f -print -not -path "*/test/*" | xargs grep -lw '@Configuration' | xargs grep -lw '@Bean' | wc -l)
              local nb_beans_xml=$(find $workingDir/$repoDir/$projectDir -name '*.xml' -type f -print -not -path "*/test/*" | xargs cat | grep -wo "<bean" | wc -l)
              local nb_beans_java=$(find $workingDir/$repoDir/$projectDir -name '*.java' -type f -print -not -path "*/test/*" | xargs cat | grep -wo "@Bean" | wc -l)
              local nb_total_beans=$(( $nb_beans_xml + $nb_beans_java ))
              local nb_total_files_beans_config=$(( $nb_xml_bean_config + $nb_java_bean_config ))

              echo "Generation report_imt_beans.txt"
              echo -e "project_name,project_path,nb_XML_files_beans_config,nb_Java_files_beans_config,nb_total_files_beans_config,nb_XML_beans,nb_Java_beans,nb_total_beans" > $report_path_additional || exit
              echo -e "$repoDir/$projectDir,$workingDir/$repoDir/$projectDir,$nb_xml_bean_config,$nb_java_bean_config,$nb_total_files_beans_config,$nb_beans_xml,$nb_beans_java,$nb_total_beans" >> $report_path_additional || exit
              echo -e "$workingDir/$repoDir/$projectDir" >> projects_list.txt || exit
              echo "--> Creation process OK, exit directory $repoDir/$projectDir <--"
            fi
        done
    done
}

#Main
[ $1 ] && {
  if ! [ -x "$(command -v cloc)" ]; then
    echo 'Error: cloc is not installed.' >&2
    exit 1
  fi
  if ! [ -x "$(command -v cloc)" ]; then
    echo 'Error: cloc is not installed.' >&2
    exit 1
  fi
   CreateReportsProjects $1
   #echo $(decrement 12)
} || {
   echo "You must indicate the folder to work and extract the statistics"
}
