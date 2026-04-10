FROM amazoncorretto:26

WORKDIR /app

RUN if command -v dnf >/dev/null 2>&1; then \
      dnf install -y findutils && dnf clean all; \
    elif command -v yum >/dev/null 2>&1; then \
      yum install -y findutils && yum clean all; \
    else \
      echo "No supported package manager found" >&2; \
      exit 1; \
    fi

COPY . /app

CMD ["bash"]
