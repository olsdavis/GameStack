# -*- coding: utf8 -*-
from util.timehelper import current_time_millis, log_file_name
from util import log
import gamestack
from rmq import rabbitstack
from config import configuration


version = "1.0.0"


class MyListener(rabbitstack.RabbitListener):
    def call(self, channel, body):
        print 'Received {} on channel {}'.format(channel, body)


def main():
    logger = log.Logger(log_file=log_file_name())
    logger.log('Starting up GameStack client version {}'.format(version))
    start_time = current_time_millis()

    # begin

    configured = configuration.load('config.yml')
    core = gamestack.GameStack(logger)

    # end

    time_diff = current_time_millis() - start_time
    logger.log('Done in {0}ms (~{1}s).'.format(time_diff, round(time_diff / 1000, 1)))


# Run
if __name__ == "__main__":
    main()
