#!/bin/sh

../gradlew publishLinux64PublicationToMavenRepository -PSONATYPE_USERNAME=$SONATYPE_USERNAME -PSONATYPE_PASSWORD=$SONATYPE_PASSWORD
../gradlew publishLinux32PublicationToMavenRepository -PSONATYPE_USERNAME=$SONATYPE_USERNAME -PSONATYPE_PASSWORD=$SONATYPE_PASSWORD
