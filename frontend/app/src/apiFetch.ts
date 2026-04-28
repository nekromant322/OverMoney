export async function apiFetch(input: RequestInfo, init?: RequestInit): Promise<Response> {
  const response = await fetch(input, init);
  if (response.status === 401 || response.status === 403) {
    window.location.href = `${import.meta.env.BASE_URL}login`;
  }
  return response;
}
