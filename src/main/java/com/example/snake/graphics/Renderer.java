package com.example.snake.graphics;

import java.util.Collection;

import com.example.snake.game.GameEnvironment;
import com.example.snake.model.Food;
import com.example.snake.model.FoodType;
import com.example.snake.model.GridPoint;
import com.example.snake.model.Snake;
import com.example.snake.utils.GameColor;
import com.example.snake.utils.IOUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.Collection;

public class Renderer {

  private static final boolean DRAW_GRID = false;

  private final Canvas canvas;

  private final Image snakeHead;
  private final Image snakeTongue;
  private final Image[] snakeBodyParts;
  private final Image heartFood;
  private final Image preyFood;

  public Renderer(Canvas canvas) {
    this.canvas = canvas;
    this.snakeBodyParts = new Image[3];
    this.snakeBodyParts[0] = IOUtils.loadImage("/snake-body-0.png");
    this.snakeBodyParts[1] = IOUtils.loadImage("/snake-body-1.png");
    this.snakeBodyParts[2] = IOUtils.loadImage("/snake-body-2.png");

    this.snakeHead = IOUtils.loadImage("/snake-head.png");
    this.snakeTongue = IOUtils.loadImage("/tongue.png");
    this.heartFood = IOUtils.loadImage("/FoodBox.png");
    this.preyFood = IOUtils.loadImage("/prey-1.png");
  }

  public void draw(GameEnvironment gameEnvironment) {
    GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
    int gameFieldWidth = gameEnvironment.getGameFieldWidth();
    int gameFieldHeight = gameEnvironment.getGameFieldHeight();
    double cellWidth = canvas.getWidth() / gameFieldWidth;
    double cellHeight = canvas.getHeight() / gameFieldHeight;

    // Set the background to pure black. Done by filling with a black rectangle since the clear color
    // in JavaFX seems to be white
    graphicsContext2D.setFill(Color.valueOf(GameColor.DARK_GREY.getHexValue()));
    graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

    // Draw a grid to help visualize for debugging purposes
    if (DRAW_GRID) {
      drawGrid(graphicsContext2D, gameFieldWidth, gameFieldHeight, cellWidth, cellHeight);
    }

    drawSnake(graphicsContext2D, gameEnvironment.getSnake(), cellWidth, cellHeight);
    drawFood(graphicsContext2D, gameEnvironment.getFoods(), cellWidth, cellHeight);
    renderWalls(graphicsContext2D, gameEnvironment, cellWidth, cellHeight);
  }

  private void drawGrid(GraphicsContext graphicsContext2D,
                        int gameFieldWidth,
                        int gameFieldHeight,
                        double cellWidth,
                        double cellHeight) {
    graphicsContext2D.setStroke(Color.WHITE);

    for (int x = 0; x < gameFieldWidth; x++) {
      graphicsContext2D.strokeLine(x * cellWidth, 0, x * cellWidth, canvas.getHeight());
    }
    for (int y = 0; y < gameFieldHeight; y++) {
      graphicsContext2D.strokeLine(0, y * cellHeight, canvas.getWidth(), y * cellWidth);
    }
  }

  private void drawSnake(GraphicsContext graphicsContext2D,
                         Snake snake,
                         double cellWidth,
                         double cellHeight) {
    GridPoint head = snake.getHead();

    graphicsContext2D.save();
    float rotation = switch (snake.getDirection()) {
      case RIGHT -> 90.0F;
      case LEFT -> 270.0f;
      case UP -> 0.0F;
      case DOWN -> 180.0F;
    };
    rotate(graphicsContext2D, rotation, head.x() * cellWidth + cellWidth / 2, head.y() * cellHeight + cellHeight / 2.0);
    graphicsContext2D.drawImage(snakeHead, head.x() * cellWidth, head.y() * cellHeight, cellWidth, cellHeight);
    graphicsContext2D.drawImage(snakeTongue, head.x() * cellWidth, (head.y() - 1) * cellHeight, cellWidth, cellHeight);
    graphicsContext2D.restore();

    for (int i = 1; i < snake.getSize(); i++) {
      GridPoint bodyPart = snake.getPoint(i);
      graphicsContext2D.drawImage(snakeBodyParts[i % snakeBodyParts.length], bodyPart.x() * cellWidth, bodyPart.y() * cellHeight, cellWidth, cellHeight);
    }
  }

  private void drawFood(GraphicsContext graphicsContext2D,
                        Collection<Food> foods,
                        double cellWidth,
                        double cellHeight) {

    for (Food food : foods) {
      GridPoint position = food.getPosition();
      FoodType foodType = food.getFoodType();
      if (foodType == FoodType.PREY) {
        graphicsContext2D.drawImage(preyFood, position.x() * cellWidth, position.y() * cellHeight, cellWidth, cellHeight);

//        graphicsContext2D.setFill(Color.ANTIQUEWHITE);
//        graphicsContext2D.fillRoundRect(position.x() * cellWidth, position.y() * cellHeight, cellWidth, cellHeight, cellWidth * 0.5, cellHeight * 0.5);
      } else {
        graphicsContext2D.drawImage(heartFood, position.x() * cellWidth, position.y() * cellHeight, cellWidth, cellHeight);
      }
    }
  }

  private void rotate(GraphicsContext gc, double angle, double pivotX, double pivotY) {
    Rotate r = new Rotate(angle, pivotX, pivotY);
    gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
  }

  private void renderWalls(GraphicsContext graphicsContext2D,
                           GameEnvironment gameEnvironment,
                           double cellWidth,
                           double cellHeight) {
    if (gameEnvironment.hasEdgeWalls()) {
      // TODO: Use GameColor enum?
      graphicsContext2D.setFill(Color.DARKRED);
      for (int x = 0; x < gameEnvironment.getGameFieldWidth(); x++) {
        graphicsContext2D.fillRect(x * cellWidth, 0, cellWidth, cellHeight);
        graphicsContext2D.fillRect(x * cellWidth, (gameEnvironment.getGameFieldHeight() - 1) * cellHeight, cellWidth, cellHeight);
      }

      for (int y = 0; y < gameEnvironment.getGameFieldHeight(); y++) {
        graphicsContext2D.fillRect(0, y * cellHeight, cellWidth, cellHeight);
        graphicsContext2D.fillRect((gameEnvironment.getGameFieldWidth() - 1) * cellWidth, y * cellHeight, cellWidth, cellHeight);
      }
    }
  }
}
