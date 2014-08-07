#! /usr/bin/env python
import argparse
import os
import sys

from common import commandlineexecutor

SCRIPT_VERSION = '%s : 3.0.5.0' % os.path.basename(__file__)

DESCRIPTION = '''
Intelligence Portal extensibility sample development script, this script supports extension of the web UI.
'''

EPILOG = '''
The following tasks are available, if none are specified then "dev-unpack-xap" will run:
    dev-unpack-xap - unpacks the xap into the ui extensibility-sample extractedxap folder.
    dev-update-xap-fragment - Copies the contents of the ui extensibility-sample build output folder to the xap fragment for inclusion in the ear.
'''

ADDITIONAL_HELP = '''
None.
'''

parser = argparse.ArgumentParser(formatter_class=argparse.RawDescriptionHelpFormatter,
                                 description=DESCRIPTION,
                                 epilog=EPILOG)
parser.add_argument('-t',
                    default='dev-unpack-xap',
                    dest='task',
                    help="the task to perform, this will default to 'dev-unpack-xap'")

executor = commandlineexecutor.Executor(parser, 'dev-ui.xml', SCRIPT_VERSION, ADDITIONAL_HELP)
executor.execute(sys.argv[1:] + ['-s', 'write'])
