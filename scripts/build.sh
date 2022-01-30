PROJECT_NAME=ehallmarksolutions
IMAGE_NAME="sports-scraper:latest"
FULL_IMAGE_NAME="gcr.io/$PROJECT_NAME/$IMAGE_NAME"

echo "Building image"
docker build -t $FULL_IMAGE_NAME -f Dockerfile .

docker push $FULL_IMAGE_NAME