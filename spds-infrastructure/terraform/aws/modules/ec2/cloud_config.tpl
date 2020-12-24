users:
  - name: impads
    shell: /bin/bash
    sudo: ALL=(ALL) NOPASSWD:ALL
    ssh-authorized-keys:
      - ${tpl_ssh_authorized_keys}