#!/usr/bin/python
# -*- coding: utf-8 -*-
import logging
import os
import subprocess

import requests

__author__ = 'dnanhkhoa'

VNCORENLP_SERVER = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)),
                                                 '../bin/VnCoreNLPServer.jar'))


class VnCoreNLP(object):
    def __init__(self, quiet=True):
        self.quiet = quiet
        self.logger = logging.getLogger(__name__)

        if not os.path.isfile(VNCORENLP_SERVER):
            raise FileNotFoundError('File "VnCoreNLPServer.jar" was not found, please re-install this package.')

        if subprocess.call(['java', '-version'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, shell=True):
            raise FileNotFoundError('Java was not found, please install JRE 1.8 first.')

        args = ['java', '-Xmx2g', '-jar', VNCORENLP_SERVER]
        self.process = subprocess.call(args)

    def close(self):
        if self.process:
            self.logger.info(__class__.__name__ + ': cleaning up...')
            self.logger.info(__class__.__name__ + ': killing VnCoreNLPServer process (%s)...' % self.process.pid)

            # Kill process
            self.process.kill()
            self.process = None

            self.logger.info(__class__.__name__ + ': done.')

    def is_alive(self):
        response = requests.get('http://112.213.86.221:9000')
        return response.status_code == requests.codes.ok

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()

    def annotate(self, text, annotators):
        response = requests.post()

    def tokenize(self, text):
        pass

    def pos_tag(self, text):
        pass

    def ner(self, text):
        pass

    def dep_parse(self, text):
        pass
