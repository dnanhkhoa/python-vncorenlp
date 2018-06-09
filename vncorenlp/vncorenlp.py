#!/usr/bin/python
# -*- coding: utf-8 -*-
import logging
import os
import socket
import subprocess
import time
from urllib.parse import urlparse

import requests
from requests.exceptions import RequestException

__author__ = 'dnanhkhoa'

VNCORENLP_SERVER = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'bin/VnCoreNLPServer.jar'))


class VnCoreNLP(object):
    def __init__(self, address='http://127.0.0.1', port=None, timeout=30, annotators='wseg,pos,ner,parse',
                 max_heap_size='-Xmx2g', quiet=True):

        # Add logger
        self.logger = logging.getLogger(__name__)

        # Get a random port if port is not set
        if port is None:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.bind(('', 0))
                port = s.getsockname()[1]

        # Default URL
        self.url = 'http://127.0.0.1:' + str(port)
        self.timeout = timeout

        # Default process
        self.process = None

        if address.startswith('http'):
            o = urlparse(address)
            self.url = '%s://%s:%d' % (o.scheme, o.netloc, port)
            self.logger.info('Using an existing server: %s' % self.url)
        else:
            # Check if VnCoreNLP file exists
            if not os.path.isfile(address):
                raise FileNotFoundError('File "%s" was not found, please check again.' % address)

            # Check if VnCoreNLPServer file exists
            if not os.path.isfile(VNCORENLP_SERVER):
                raise FileNotFoundError('File "%s" was not found, please re-install this package.' % VNCORENLP_SERVER)

            # Check if Java exists
            if subprocess.call(['java', '-version'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, shell=True):
                raise FileNotFoundError('Java was not found, please install JRE or JDK 1.8 first.')

            # Start the server
            self.logger.info('Starting server on: %s' % self.url)

            args = {
                'args': ['java', max_heap_size, '-jar', VNCORENLP_SERVER, address, '-i',
                         urlparse(self.url).netloc.split(':')[0], '-p', str(port), '-a', annotators]
            }
            if quiet:
                args['stdout'] = subprocess.DEVNULL
                args['stderr'] = subprocess.DEVNULL

            self.process = subprocess.Popen(**args)
            self.logger.info('Server ID: %d' % self.process.pid)

        # Waiting until the server is available
        attempts = 0
        while attempts < 100 and not self.is_alive():
            if self.process and self.process.poll():
                raise RuntimeError('The server has stopped working.')
            self.logger.info('Waiting until the server is available...')
            attempts += 1
            time.sleep(5)

        # Store the annotators getting from the server
        self.annotators = set(self.__get_annotators() + ['lang'])
        self.logger.info('The server is now available on: %s' % self.url)

    def close(self):
        # Stop the server and clean up
        if self.process:
            self.logger.info(__class__.__name__ + ': cleaning up...')
            self.logger.info(__class__.__name__ + ': killing the server process (%s)...' % self.process.pid)

            # Kill process
            self.process.kill()
            self.process = None

            self.logger.info(__class__.__name__ + ': done.')

    def is_alive(self):
        # Check if the server is alive
        try:
            response = requests.get(self.url, timeout=self.timeout)
            return response.ok
        except RequestException:
            pass
        return False

    def __get_annotators(self):
        # Get list of annotators from the server
        response = requests.get(self.url + '/annotators', timeout=self.timeout)
        response.raise_for_status()
        return response.json()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()

    def annotate(self, text, annotators=None):
        if isinstance(annotators, str):
            assert self.annotators.issuperset(annotators.split(
                ',')), 'Please ensure that the annotators "%s" are being used on the server.' % annotators
        data = {
            'text': text.encode('UTF-8'),
            'props': annotators
        }
        response = requests.post(self.url + '/handle', data=data, timeout=self.timeout)
        response.raise_for_status()
        response = response.json()
        assert response['status'], response['error']
        del response['status']
        return response

    def tokenize(self, text):
        sentences = self.annotate(text, annotators='wseg')['sentences']
        return [[w['form'] for w in s] for s in sentences]

    def pos_tag(self, text):
        sentences = self.annotate(text, annotators='wseg,pos')['sentences']
        return [[(w['form'], w['posTag']) for w in s] for s in sentences]

    def ner(self, text):
        sentences = self.annotate(text, annotators='wseg,pos,ner')['sentences']
        return [[(w['form'], w['nerLabel']) for w in s] for s in sentences]

    def dep_parse(self, text):
        sentences = self.annotate(text, annotators='wseg,pos,ner,parse')['sentences']
        # dep, governor, dependent
        return [[(w['depLabel'], w['head'], w['index']) for w in s] for s in sentences]

    def detect_language(self, text):
        return self.annotate(text, annotators='lang')['language']
