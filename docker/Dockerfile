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

RUN printf "%s\n" "alias merge='java -cp /app/build/libs/pdf_tools.jar MergePDF'" >> /root/.bashrc

RUN printf "%s\n" "alias split='java -cp /app/build/libs/pdf_tools.jar SplitPDF'" >> /root/.bashrc

COPY . /app

CMD ["bash"]
