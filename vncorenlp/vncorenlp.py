#!/usr/bin/python
# -*- coding: utf-8 -*-
import logging
import os
import re
import subprocess

import requests
from requests.exceptions import RequestException

__author__ = 'dnanhkhoa'

VNCORENLP_SERVER = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)),
                                                 '../bin/VnCoreNLPServer.jar'))


class VnCoreNLP(object):
    def __init__(self, address='0.0.0.0', port=None, timeout=30, annotators='wseg,pos,ner,parse', quiet=True):
        self.logger = logging.getLogger(__name__)

        self.annotators = re.split('\s*,\s*', annotators.strip())
        self.url = None
        self.timeout = timeout

        if not os.path.isfile(VNCORENLP_SERVER):
            raise FileNotFoundError('File "VnCoreNLPServer.jar" was not found, please re-install this package.')

        if subprocess.call(['java', '-version'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, shell=True):
            raise FileNotFoundError('Java was not found, please install JRE 1.8 first.')

        args = ['java', '-Xmx2g', '-jar', VNCORENLP_SERVER]
        self.process = None  # subprocess.Popen(args)

    def close(self):
        if self.process:
            self.logger.info(__class__.__name__ + ': cleaning up...')
            self.logger.info(__class__.__name__ + ': killing VnCoreNLPServer process (%s)...' % self.process.pid)

            # Kill process
            self.process.kill()
            self.process = None

            self.logger.info(__class__.__name__ + ': done.')

    def is_alive(self):
        try:
            response = requests.get(url=self.url, timeout=self.timeout)
            response.raise_for_status()
            return response.status_code == requests.codes.ok
        except RequestException as e:
            self.logger.exception(e)
        return False

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
