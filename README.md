# python-vncorenlp

[![PyPI](https://img.shields.io/pypi/v/vncorenlp.svg)]()
[![PyPI - Python Version](https://img.shields.io/pypi/pyversions/vncorenlp.svg)]()

A Python wrapper for [VnCoreNLP](https://github.com/vncorenlp/VnCoreNLP) using a bidirectional communication channel.

## Table Of Contents

  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
  * [Example Usage](#example-usage)
  * [Use An Existing Server](#use-an-existing-server)
  * [Debug](#debug)
  * [Some Use Cases](#some-use-cases)
  * [License](#license)

## Prerequisites

- Java 1.8+ ([JRE](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) or [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
- VnCoreNLP ([Github](https://github.com/vncorenlp/VnCoreNLP) or [Download](https://github.com/vncorenlp/VnCoreNLP/archive/master.zip))

## Installation

You can install this package from PyPI using [pip](http://www.pip-installer.org):

```
$ [sudo] pip install vncorenlp
```

For Windows users, please ensure that you run the `Command Prompt` with **admin** privileges.

## Example Usage

A simple example of how to use `vncorenlp`:

```python
#!/usr/bin/python
# -*- coding: utf-8 -*-
import logging

from vncorenlp import VnCoreNLP


def simple_usage():
    # Uncomment this line for debugging
    # logging.basicConfig(level=logging.DEBUG)

    vncorenlp_file = r'.../VnCoreNLP-1.0.1/VnCoreNLP-1.0.1.jar'

    sentences = 'VTV đồng ý chia sẻ bản quyền World Cup 2018 cho HTV để khai thác. ' \
                'Nhưng cả hai nhà đài đều phải chờ sự đồng ý của FIFA mới thực hiện được điều này.'

    # Use "with ... as" to close the server automatically
    with VnCoreNLP(vncorenlp_file) as vncorenlp:
        print('Tokenizing:', vncorenlp.tokenize(sentences))
        print('POS Tagging:', vncorenlp.pos_tag(sentences))
        print('Named-Entity Recognizing:', vncorenlp.ner(sentences))
        print('Dependency Parsing:', vncorenlp.dep_parse(sentences))
        print('Annotating:', vncorenlp.annotate(sentences))
        print('Language:', vncorenlp.detect_language(sentences))

    # In this way, you have to close the server manually by calling close function
    vncorenlp = VnCoreNLP(vncorenlp_file)

    print('Tokenizing:', vncorenlp.tokenize(sentences))
    print('POS Tagging:', vncorenlp.pos_tag(sentences))
    print('Named-Entity Recognizing:', vncorenlp.ner(sentences))
    print('Dependency Parsing:', vncorenlp.dep_parse(sentences))
    print('Annotating:', vncorenlp.annotate(sentences))
    print('Language:', vncorenlp.detect_language(sentences))

    # Do not forget to close the server
    vncorenlp.close()


if __name__ == '__main__':
    simple_usage()
```

And here is the output:

```
Tokenizing:
[
    ['VTV', 'đồng_ý', 'chia_sẻ', 'bản_quyền', 'World_Cup', '2018', 'cho', 'HTV', 'để', 'khai_thác', '.'],
    ['Nhưng', 'cả', 'hai', 'nhà', 'đài', 'đều', 'phải', 'chờ', 'sự', 'đồng_ý', 'của', 'FIFA', 'mới', 'thực_hiện', 'được', 'điều', 'này', '.']
]
 
 
POS Tagging:
[
    [('VTV', 'Ny'), ('đồng_ý', 'V'), ('chia_sẻ', 'V'), ('bản_quyền', 'N'), ('World_Cup', 'N'), ('2018', 'M'), ('cho', 'E'), ('HTV', 'Ny'), ('để', 'E'), ('khai_thác', 'V'), ('.', 'CH')],
    [('Nhưng', 'C'), ('cả', 'P'), ('hai', 'M'), ('nhà', 'N'), ('đài', 'N'), ('đều', 'R'), ('phải', 'V'), ('chờ', 'V'), ('sự', 'Nc'), ('đồng_ý', 'V'), ('của', 'E'), ('FIFA', 'Np'), ('mới', 'R'), ('thực_hiện', 'V'), ('được', 'R'), ('điều', 'N'), ('này', 'P'), ('.', 'CH')]
]
 
 
Named-Entity Recognizing:
[
    [('VTV', 'O'), ('đồng_ý', 'O'), ('chia_sẻ', 'O'), ('bản_quyền', 'O'), ('World_Cup', 'O'), ('2018', 'O'), ('cho', 'O'), ('HTV', 'O'), ('để', 'O'), ('khai_thác', 'O'), ('.', 'O')],
    [('Nhưng', 'O'), ('cả', 'O'), ('hai', 'O'), ('nhà', 'O'), ('đài', 'O'), ('đều', 'O'), ('phải', 'O'), ('chờ', 'O'), ('sự', 'O'), ('đồng_ý', 'O'), ('của', 'O'), ('FIFA', 'B-ORG'), ('mới', 'O'), ('thực_hiện', 'O'), ('được', 'O'), ('điều', 'O'), ('này', 'O'), ('.', 'O')]
]
 
 
Dependency Parsing:
[
    [('sub', 2, 1), ('root', 0, 2), ('vmod', 2, 3), ('dob', 3, 4), ('nmod', 4, 5), ('det', 5, 6), ('iob', 3, 7), ('pob', 7, 8), ('prp', 3, 9), ('vmod', 9, 10), ('punct', 2, 11)],
    [('dep', 7, 1), ('nmod', 4, 2), ('det', 4, 3), ('sub', 7, 4), ('nmod', 4, 5), ('adv', 7, 6), ('root', 0, 7), ('vmod', 7, 8), ('dob', 8, 9), ('nmod', 9, 10), ('nmod', 9, 11), ('pob', 11, 12), ('adv', 14, 13), ('vmod', 7, 14), ('adv', 14, 15), ('dob', 14, 16), ('det', 16, 17), ('punct', 7, 18)]
]
 
 
Annotating:
{
  "sentences": [
    [
      {
        "index": 1,
        "form": "VTV",
        "posTag": "Ny",
        "nerLabel": "O",
        "head": 2,
        "depLabel": "sub"
      },
      {
        "index": 2,
        "form": "đồng_ý",
        "posTag": "V",
        "nerLabel": "O",
        "head": 0,
        "depLabel": "root"
      },
      {
        "index": 3,
        "form": "chia_sẻ",
        "posTag": "V",
        "nerLabel": "O",
        "head": 2,
        "depLabel": "vmod"
      },
      {
        "index": 4,
        "form": "bản_quyền",
        "posTag": "N",
        "nerLabel": "O",
        "head": 3,
        "depLabel": "dob"
      },
      {
        "index": 5,
        "form": "World_Cup",
        "posTag": "N",
        "nerLabel": "O",
        "head": 4,
        "depLabel": "nmod"
      },
      {
        "index": 6,
        "form": "2018",
        "posTag": "M",
        "nerLabel": "O",
        "head": 5,
        "depLabel": "det"
      },
      {
        "index": 7,
        "form": "cho",
        "posTag": "E",
        "nerLabel": "O",
        "head": 3,
        "depLabel": "iob"
      },
      {
        "index": 8,
        "form": "HTV",
        "posTag": "Ny",
        "nerLabel": "O",
        "head": 7,
        "depLabel": "pob"
      },
      {
        "index": 9,
        "form": "để",
        "posTag": "E",
        "nerLabel": "O",
        "head": 3,
        "depLabel": "prp"
      },
      {
        "index": 10,
        "form": "khai_thác",
        "posTag": "V",
        "nerLabel": "O",
        "head": 9,
        "depLabel": "vmod"
      },
      {
        "index": 11,
        "form": ".",
        "posTag": "CH",
        "nerLabel": "O",
        "head": 2,
        "depLabel": "punct"
      }
    ],
    [
      {
        "index": 1,
        "form": "Nhưng",
        "posTag": "C",
        "nerLabel": "O",
        "head": 7,
        "depLabel": "dep"
      },
      {
        "index": 2,
        "form": "cả",
        "posTag": "P",
        "nerLabel": "O",
        "head": 4,
        "depLabel": "nmod"
      },
      {
        "index": 3,
        "form": "hai",
        "posTag": "M",
        "nerLabel": "O",
        "head": 4,
        "depLabel": "det"
      },
      {
        "index": 4,
        "form": "nhà",
        "posTag": "N",
        "nerLabel": "O",
        "head": 7,
        "depLabel": "sub"
      },
      {
        "index": 5,
        "form": "đài",
        "posTag": "N",
        "nerLabel": "O",
        "head": 4,
        "depLabel": "nmod"
      },
      {
        "index": 6,
        "form": "đều",
        "posTag": "R",
        "nerLabel": "O",
        "head": 7,
        "depLabel": "adv"
      },
      {
        "index": 7,
        "form": "phải",
        "posTag": "V",
        "nerLabel": "O",
        "head": 0,
        "depLabel": "root"
      },
      {
        "index": 8,
        "form": "chờ",
        "posTag": "V",
        "nerLabel": "O",
        "head": 7,
        "depLabel": "vmod"
      },
      {
        "index": 9,
        "form": "sự",
        "posTag": "Nc",
        "nerLabel": "O",
        "head": 8,
        "depLabel": "dob"
      },
      {
        "index": 10,
        "form": "đồng_ý",
        "posTag": "V",
        "nerLabel": "O",
        "head": 9,
        "depLabel": "nmod"
      },
      {
        "index": 11,
        "form": "của",
        "posTag": "E",
        "nerLabel": "O",
        "head": 9,
        "depLabel": "nmod"
      },
      {
        "index": 12,
        "form": "FIFA",
        "posTag": "Np",
        "nerLabel": "B-ORG",
        "head": 11,
        "depLabel": "pob"
      },
      {
        "index": 13,
        "form": "mới",
        "posTag": "R",
        "nerLabel": "O",
        "head": 14,
        "depLabel": "adv"
      },
      {
        "index": 14,
        "form": "thực_hiện",
        "posTag": "V",
        "nerLabel": "O",
        "head": 7,
        "depLabel": "vmod"
      },
      {
        "index": 15,
        "form": "được",
        "posTag": "R",
        "nerLabel": "O",
        "head": 14,
        "depLabel": "adv"
      },
      {
        "index": 16,
        "form": "điều",
        "posTag": "N",
        "nerLabel": "O",
        "head": 14,
        "depLabel": "dob"
      },
      {
        "index": 17,
        "form": "này",
        "posTag": "P",
        "nerLabel": "O",
        "head": 16,
        "depLabel": "det"
      },
      {
        "index": 18,
        "form": ".",
        "posTag": "CH",
        "nerLabel": "O",
        "head": 7,
        "depLabel": "punct"
      }
    ]
  ]
}
 
 
Language: vi
```

## Use An Existing Server

**I highly recommend you using this approach to save your time when you are debugging your code.**

First, you need to start the VnCoreNLPServer using this command:

```
$ vncorenlp -Xmx2g <VnCoreNLP File> -p 9000 -a "wseg,pos,ner,parse"
```

The parameter `-Xmx2g` means that the VM can allocate a maximum of 2 GB for the Heap Space.

And then connect to the server using this code:

```python
# Use the existing server
with VnCoreNLP(address='http://127.0.0.1', port=9000) as vncorenlp:
    ...
```

## Debug

There are 3 ways to enable debugging:

```python
#!/usr/bin/python
# -*- coding: utf-8 -*-
import logging
import sys

from vncorenlp import VnCoreNLP


# 1. Use the global logger
# logging.basicConfig(level=logging.DEBUG)

def simple_usage():
    vncorenlp_file = r'.../VnCoreNLP-1.0.1/VnCoreNLP-1.0.1.jar'

    sentences = 'VTV đồng ý chia sẻ bản quyền World Cup 2018 cho HTV để khai thác. ' \
                'Nhưng cả hai nhà đài đều phải chờ sự đồng ý của FIFA mới thực hiện được điều này.'

    # Use "with ... as" to close the server automatically
    vncorenlp = VnCoreNLP(vncorenlp_file)

    # 2. Set up the local logger here
    logger = vncorenlp.logger
    logger.setLevel(logging.DEBUG)
    # Add stdout
    ch = logging.StreamHandler(sys.stdout)
    ch.setLevel(logging.DEBUG)
    # Add formatter
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    ch.setFormatter(formatter)
    logger.addHandler(ch)

    with vncorenlp:
        print('Tokenizing:', vncorenlp.tokenize(sentences))
        print('POS Tagging:', vncorenlp.pos_tag(sentences))
        print('Named-Entity Recognizing:', vncorenlp.ner(sentences))
        print('Dependency Parsing:', vncorenlp.dep_parse(sentences))
        print('Annotating:', vncorenlp.annotate(sentences))
        print('Language:', vncorenlp.detect_language(sentences))

    # In this way, you have to close the server manually by calling close function
    vncorenlp = VnCoreNLP(vncorenlp_file)

    # 3. Set up the local logger here
    logger = vncorenlp.logger
    logger.setLevel(logging.DEBUG)
    # Add stdout
    ch = logging.StreamHandler(sys.stdout)
    ch.setLevel(logging.DEBUG)
    # Add formatter
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    ch.setFormatter(formatter)
    logger.addHandler(ch)

    print('Tokenizing:', vncorenlp.tokenize(sentences))
    print('POS Tagging:', vncorenlp.pos_tag(sentences))
    print('Named-Entity Recognizing:', vncorenlp.ner(sentences))
    print('Dependency Parsing:', vncorenlp.dep_parse(sentences))
    print('Annotating:', vncorenlp.annotate(sentences))
    print('Language:', vncorenlp.detect_language(sentences))

    # Do not forget to close the server
    vncorenlp.close()


if __name__ == '__main__':
    simple_usage()
```

## Some Use Cases

```python
#!/usr/bin/python
# -*- coding: utf-8 -*-
import logging

from vncorenlp import VnCoreNLP

logging.basicConfig(level=logging.DEBUG)


def simple_usage():
    vncorenlp_file = r'.../VnCoreNLP-1.0.1/VnCoreNLP-1.0.1.jar'

    sentences = 'VTV đồng ý chia sẻ bản quyền World Cup 2018 cho HTV để khai thác. ' \
                'Nhưng cả hai nhà đài đều phải chờ sự đồng ý của FIFA mới thực hiện được điều này.'

    # Use only word segmentation
    with VnCoreNLP(vncorenlp_file, annotators="wseg") as vncorenlp:
        print('Tokenizing:', vncorenlp.tokenize(sentences))

    # Specify the maximum heap size
    with VnCoreNLP(vncorenlp_file, annotators="wseg", max_heap_size='-Xmx4g') as vncorenlp:
        print('Tokenizing:', vncorenlp.tokenize(sentences))

    # For debugging
    with VnCoreNLP(vncorenlp_file, annotators="wseg", max_heap_size='-Xmx4g', quiet=False) as vncorenlp:
        print('Tokenizing:', vncorenlp.tokenize(sentences))


if __name__ == '__main__':
    simple_usage()
```

## License

MIT
