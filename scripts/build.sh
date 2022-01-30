
IMAGE_NAME="sports-scraper:latest"

echo "Building image"
docker build -t $IMAGE_NAME -f Dockerfile .
