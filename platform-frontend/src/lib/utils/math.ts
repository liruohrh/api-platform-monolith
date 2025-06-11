export function commentScore(score: number): number {
  return Number((score / 2).toFixed(1));
}
export function apiScore(score: number): number {
  return Number((score / 20).toFixed(1));
}
