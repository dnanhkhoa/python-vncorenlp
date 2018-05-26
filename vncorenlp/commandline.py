#!/usr/bin/python
# -*- coding: utf-8 -*-
import subprocess
import sys

from vncorenlp import VnCoreNLPServer


def main():
    try:
        if subprocess.call(['java', '-version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True):
            raise FileNotFoundError('Java was not found, please install first.')

        args = ['java', '-Xmx2g', '-jar', VnCoreNLPServer]

        if len(sys.argv) > 1:
            pos = 1
            if sys.argv[pos].startswith('-Xmx'):
                args[pos] = sys.argv[pos]
                pos += 1
            args.extend(sys.argv[pos:])

        subprocess.call(args)
    except KeyboardInterrupt:
        pass
