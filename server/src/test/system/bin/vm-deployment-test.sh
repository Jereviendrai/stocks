#!/bin/bash

set -e

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

# virsh reset
sudo virsh snapshot-revert dp-server clean-running
sleep 1

ansible-playbook $STOCKS_ROOT/deploy-server/install.yml
ansible-playbook $STOCKS_ROOT/deploy-server/deploy.yml

sleep 10

$STOCKS_ROOT/server/src/test/system/bin/fresh-installation-test.sh dp-server

sudo virsh snapshot-revert dp-server clean

