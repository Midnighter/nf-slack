#!/usr/bin/env nextflow

nextflow.enable.dsl = 2

include { notifySlack } from 'plugin/nf-slack'


process FOO {
    """
    echo 'Hello World'
    """
}


workflow {
    FOO()
}

workflow.onComplete {
    notifySlack()
}
