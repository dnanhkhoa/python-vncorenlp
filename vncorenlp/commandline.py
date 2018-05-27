#!/usr/bin/python
# -*- coding: utf-8 -*-
import os
import subprocess
import sys

from vncorenlp import VNCORENLP_SERVER


def main():
    try:
        if not os.path.isfile(VNCORENLP_SERVER):
            raise FileNotFoundError('File "VnCoreNLPServer.jar" was not found, please re-install this package.')

        if subprocess.call(['java', '-version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True):
            raise FileNotFoundError('Java was not found, please install first.')

        args = ['java', '-Xmx2g', '-jar', VNCORENLP_SERVER]

        if len(sys.argv) > 1:
            pos = 1
            if sys.argv[pos].startswith('-Xmx'):
                args[pos] = sys.argv[pos]
                pos += 1
            args.extend(sys.argv[pos:])

        subprocess.call(args)
    except KeyboardInterrupt:
        pass
