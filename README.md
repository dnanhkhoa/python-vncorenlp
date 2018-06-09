# Python-vncorenlp

![python-vncorenlp status](https://img.shields.io/pypi/v/vncorenlp.svg)

A Python wrapper for VnCoreNLP using a bidirectional communication channel.

## Prerequisites

- Java 1.8+ (Download)

- VnCoreNLP (Download)

## Installation

You can install this package from PyPI using [pip](http://www.pip-installer.org):

```
$ [sudo] pip install vncorenlp
```

## Example Usage

Here is an example of how easy it is to get started:

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

    with VnCoreNLP(vncorenlp_file) as vncorenlp:
        print('Tokenizing:', vncorenlp.tokenize(sentences))
        print('POS Tagging:', vncorenlp.pos_tag(sentences))
        print('Named-Entity Recognizing:', vncorenlp.ner(sentences))
        print('Dependency Parsing:', vncorenlp.dep_parse(sentences))
        print('Annotating:', vncorenlp.annotate(sentences))
        print('Language:', vncorenlp.detect_language(sentences))


if __name__ == '__main__':
    simple_usage()
```

And here is terminal output:

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

## Use an Existing Server

First, you need to start the VnCoreNLPServer using this command:

```
$ vncorenlp -Xmx2g <VnCoreNLP> -p 9000 -a "wseg,pos,ner,parse"
```


## License

MIT
