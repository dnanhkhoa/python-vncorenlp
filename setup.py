#!/usr/bin/python
# -*- coding: utf-8 -*-
import os

from setuptools import setup


def readme(file_name):
    if os.path.isfile(file_name):
        with open(file_name, 'rb') as f:
            return f.read().decode('UTF-8')
    return None


setup(name='vncorenlp',
      version='1.0.2',
      description='A Python wrapper for VnCoreNLP using a bidirectional communication channel.',
      long_description=readme(file_name='README.md'),
      keywords='python-vncorenlp vncorenlp nlp vietnamese-nlp parser word-segmentation tokenizer pos-tagger '
               'named-entity-recognition ner dependency-parser',
      url='https://github.com/dnanhkhoa/python-vncorenlp',
      author='Khoa Duong',
      author_email='dnanhkhoa@live.com',
      license='MIT',
      packages=['vncorenlp'],
      include_package_data=True,
      zip_safe=False,
      install_requires=['requests'],
      entry_points={
          'console_scripts': ['vncorenlp=vncorenlp.commandline:main']
      },
      classifiers=[
          'Development Status :: 5 - Production/Stable',
          'Intended Audience :: Developers',
          'Intended Audience :: Science/Research',
          'License :: OSI Approved :: MIT License',
          'Programming Language :: Python :: 3',
          'Topic :: Text Processing :: Linguistic'
      ])
