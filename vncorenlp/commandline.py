#!/usr/bin/python
# -*- coding: utf-8 -*-
import os
import subprocess
import sys

from vncorenlp import VNCORENLP_SERVER


def main():
    try:
        # Check if server file exists
        if not os.path.isfile(VNCORENLP_SERVER):
            raise FileNotFoundError('File "%s" was not found, please re-install this package.' % VNCORENLP_SERVER)

        # Check if Java exists
        try:
            subprocess.check_call(['java', '-version'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        except FileNotFoundError:
            raise FileNotFoundError('Java was not found, please install JRE or JDK >= 1.8 first.')

        args = ['java', '-Xmx2g', '-jar', VNCORENLP_SERVER]

        if len(sys.argv) > 1:
            pos = 1
            if sys.argv[pos].startswith('-Xmx'):
                args[pos] = sys.argv[pos]
                pos += 1
            args.extend(sys.argv[pos:])

        # Start server
        subprocess.call(args)
    except KeyboardInterrupt:
        pass
